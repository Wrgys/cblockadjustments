package com.wrgy.cblockadjustments.client;

import com.wrgy.cblockadjustments.CBlockAdjustments;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber(modid = CBlockAdjustments.MOD_ID, value = Dist.CLIENT)
public class CustomCommandBlockScreen {

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.InitScreenEvent.Post event) {
        if (event.getScreen() instanceof CommandBlockEditScreen) {
            CommandBlockEditScreen screen = (CommandBlockEditScreen) event.getScreen();

            // First button for 3 coordinates
            Button relativeTransformButton = new Button(296, 198, 20, 20, new TextComponent("~"), button -> {
                try {
                    Field commandBlockEntityField = CommandBlockEditScreen.class.getDeclaredField("autoCommandBlock");
                    commandBlockEntityField.setAccessible(true);
                    CommandBlockEntity commandBlockEntity = (CommandBlockEntity) commandBlockEntityField.get(screen);
                    BlockPos blockPos = commandBlockEntity.getBlockPos();
                    String command = commandBlockEntity.getCommandBlock().getCommand();
                    String transformedCommand = transformCoordinates(command, blockPos, 3); // Convert 3 coordinates
                    commandBlockEntity.getCommandBlock().setCommand(transformedCommand);
                    screen.updateGui();
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });

            event.addListener(relativeTransformButton);
        }
    }

    private static String transformCoordinates(String command, BlockPos blockPos, int numCoordinates) {
        Pattern pattern = Pattern.compile("~?-?\\d+");
        Matcher matcher = pattern.matcher(command);
        StringBuffer sb = new StringBuffer();
        int count = 0;
        while (matcher.find() && count < numCoordinates) {
            String coordinateStr = matcher.group();
            boolean isRelative = coordinateStr.startsWith("~");
            int coordinateValue = coordinateStr.equals("~") ? 0 : Integer.parseInt(coordinateStr.replace("~", ""));
            if (isRelative) {
                // Convert relative to absolute
                int absoluteCoord = coordinateValue + (count % 3 == 0 ? blockPos.getX() : (count % 3 == 1 ? blockPos.getY() : blockPos.getZ()));
                matcher.appendReplacement(sb, Integer.toString(absoluteCoord));
            } else {
                // Convert absolute to relative
                int relativeCoord = coordinateValue - (count % 3 == 0 ? blockPos.getX() : (count % 3 == 1 ? blockPos.getY() : blockPos.getZ()));
                matcher.appendReplacement(sb, "~" + relativeCoord);
            }
            count++;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(CustomCommandBlockScreen.class);
    }
}