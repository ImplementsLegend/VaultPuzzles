package implementslegendkt.vhpuzzles;

import net.minecraft.client.Minecraft;

import java.nio.file.Path;

public class GameDirFromMinecraft {

    public static Path retrieveGameDir() throws ClassNotFoundException{
        return Minecraft.getInstance().gameDirectory.toPath();
    }
}
