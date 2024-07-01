package implementslegendkt.vhpuzzles;


import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Config {

    public static Path cfgDir = getGameDir().resolve("config").resolve("vhpuzzles");

    static {
        if(!cfgDir.toFile().exists()){
            try {
                untar(Config.class.getResourceAsStream("/configs.tar"),cfgDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static Path getGameDir() {
        var result = Path.of("");
        /*try {
            Config.class.getClassLoader().loadClass("net.minecraft.client.Minecraft");
            result = GameDirFromMinecraft.retrieveGameDir();
        }catch (ClassNotFoundException ignored){}*/
        return result;
    }


    //stolen from Internet cause i'm too lazy
    public static void untar(InputStream is, Path targetDir) throws IOException {
        targetDir = targetDir.toAbsolutePath();
        try (var zipIn = new TarArchiveInputStream(is)) {
            for (ArchiveEntry ze; (ze = zipIn.getNextEntry()) != null; ) {
                Path resolvedPath = targetDir.resolve(ze.getName()).normalize();
                if (!resolvedPath.startsWith(targetDir)) {
                    throw new RuntimeException("Entry with an illegal path: "
                            + ze.getName());
                }
                if (ze.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    Files.createDirectories(resolvedPath.getParent());
                    Files.copy(zipIn, resolvedPath);
                }
            }
        }
    }
}
