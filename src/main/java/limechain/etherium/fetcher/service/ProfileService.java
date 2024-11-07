package limechain.etherium.fetcher.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import limechain.etherium.fetcher.utils.FileUtils;

@Service
public class ProfileService {

    @Value(value = "${MENU_PATH}")
    private String menuPath;

    @Value(value = "${LOGO_PATH}")
    private String logoPath;

    public void saveMenu(UUID uuid, MultipartFile menu) throws IOException {
        if (!menu.isEmpty()) {
            Path folder = Files.createDirectories(Paths.get(menuPath));
            Path pathToFile = folder.resolve("menu-" + uuid + ".pdf");
            FileUtils.saveFile(menu, pathToFile);
        }
    }

    public void saveLogo(UUID uuid, MultipartFile logo) throws IOException {
        if (!logo.isEmpty()) {
            Path folder = Files.createDirectories(Paths.get(logoPath));
            Path pathToFile = folder.resolve("logo-" + uuid + ".jpg");
            FileUtils.saveFile(logo, pathToFile);
        }
    }
}
