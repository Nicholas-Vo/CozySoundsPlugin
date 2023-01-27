package me.nick;

import com.sun.jna.platform.FileUtils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

@Slf4j
@PluginDescriptor(
        name = "Cozy Sounds"
)
public class CozySoundsPlugin extends Plugin {
    final String NAME = "Cozy Sounds";
    final File DIR = new File(RuneLite.RUNELITE_DIR, "CozySounds");

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private SoundManager soundManager;

    @Override
    protected void startUp() {
        resetCaches(); /* Is this needed? IDK */
        initDirectories(); /* Create directories if they do not already exist */

        log.info(NAME + " has started up successfully.");
    }

    @Override
    protected void shutDown() {
        resetCaches();
        log.info(NAME + " has stopped.");
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived e) {
        Collection<ItemStack> itemsDropped = e.getItems();

        itemsDropped.forEach(itemStack -> {
            if (itemStack.getId() == ItemID.LARRANS_KEY) {
                soundManager.playCustomSound(DIR, "larrans_key.wav");
            }
        });
    }

    private void initDirectories() {
        if (!DIR.exists()) {
            if (!DIR.mkdir()) {
                log.warn("Failed to create directory " + DIR.getName() + ".");
            } else {
                log.info("Creating directory " + DIR.getName() + " .");
            }
        }

        InputStream url = getClass().getClassLoader().getResourceAsStream("larrans_key.wav");
        File theFile = new File(DIR, "larrans_key.wav");

        if (!theFile.exists()) {
            log("theFile is null, creating new larrans_key.wav.");
            try (InputStream stream = url) {
                Files.copy(stream, Paths.get(DIR.toPath().toString(), "\\larrans_key.wav"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void log(String message) {
        log.warn(message);
    }

    public void sendMessage(String message) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
    }

    private void resetCaches() {
        clientThread.invokeLater(() -> {
            client.getItemCompositionCache().reset();
            client.getItemModelCache().reset();
            client.getItemSpriteCache().reset();
        });
    }
}
