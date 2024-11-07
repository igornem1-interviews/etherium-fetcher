package limechain.etherium.fetcher.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QrCodeGenerator {

    public static void drawCenteredString(Graphics2D g, String text, int x, int y, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        x -= metrics.stringWidth(text) / 2;
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public static void create(String pathToTemplate, String link, Integer size, List<Integer[]> coordinates, String pathToResult, String[] texts,
            List<List<Integer[]>> coordinatesText, Integer textSize, String[] pathToExtraImages, List<Integer[]> coordinatesExtra) throws IOException, WriterException {

        File targetFile = new File(pathToResult);
        File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        String charset = "UTF-8";
        BitMatrix qrMatrix = new MultiFormatWriter().encode(new String(link.getBytes(charset), charset), BarcodeFormat.QR_CODE, size, size);

        BufferedImage qrImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                qrImg.setRGB(i, j, qrMatrix.get(i, j) ? Color.BLACK.getRGB() : Color.TRANSLUCENT);
            }
        }

        Image srcImg = ImageIO.read(new File(pathToTemplate));
        int srcImgWidth = srcImg.getWidth(null);
        int srcImgHeight = srcImg.getHeight(null);

        BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufImg.createGraphics();
        graphics.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);

        for (Integer[] coords : coordinates) {
            graphics.drawImage(qrImg, coords[0], coords[1], size, size, null);
        }

        if (pathToExtraImages != null && pathToExtraImages.length > 0) {
            for (int i = 0; i < pathToExtraImages.length; i++) {
                Image extraImg = ImageIO.read(new File(pathToExtraImages[i]));
                int extraImgX;
                int extraImgY;

                if (coordinatesExtra != null && coordinatesExtra.size() > i) {
                    extraImgX = coordinatesExtra.get(i)[0];
                    extraImgY = coordinatesExtra.get(i)[1];
                } else {
                    extraImgX = (srcImgWidth - extraImg.getWidth(null)) / 2;
                    extraImgY = (srcImgHeight - extraImg.getHeight(null)) / 2;
                }

                graphics.drawImage(extraImg, extraImgX, extraImgY, extraImg.getWidth(null), extraImg.getHeight(null), null);

            }
        }

        Font shareFont = new Font("Microsoft Yahei", Font.BOLD, textSize);
        Color shareColor = new Color(0, 0, 0, 255);
        graphics.setFont(shareFont);
        graphics.setColor(shareColor);

        for (int i = 0; i < texts.length; i++) {
            for (Integer[] coords : coordinatesText.get(i)) {
                drawCenteredString(graphics, texts[i], coords[0], coords[1], shareFont);
            }
        }

        graphics.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bufImg, "png", out);

        PDDocument document = new PDDocument();
        PDRectangle rec = new PDRectangle(srcImgWidth, srcImgHeight);
        PDPage page = new PDPage(rec);
        document.addPage(page);
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, out.toByteArray(), charset);
        out.close();
        PDPageContentStream contents = new PDPageContentStream(document, page);

        contents.drawImage(pdImage, 0, 0, bufImg.getWidth(), bufImg.getHeight());

        contents.close();

        FileOutputStream outPdfStream = new FileOutputStream(pathToResult);
        document.save(outPdfStream);
        outPdfStream.flush();
        outPdfStream.close();
        document.close();
    }

    private static Integer[] coords(int x, int y) {
        return new Integer[] { x, y };
    }

    private static List<Integer[]> asList(Integer[]... coords) {
        return Arrays.asList(coords);
    }

    public static void main(String[] args) throws IOException, WriterException {

        int x = 100;
        int y = 50;

        List<Integer[]> qrCoords = asList(coords(x, y));

        List<Integer[]> sloganCoords = asList(coords(600, y + 80));

        List<Integer[]> numberCoords = asList(coords(600, y + 1100));

        List<List<Integer[]>> textCoords = new ArrayList<List<Integer[]>>(2);
        textCoords.add(sloganCoords);
        textCoords.add(numberCoords);

        call(qrCoords, textCoords, "A4-P", new String[] { "Rest", "123456" }, 1050, 50, new String[] { "./../data/qr-templates/logo.jpg" }, null);

    }

    private static void call(List<Integer[]> qrCoords, List<List<Integer[]>> textCoords, String tmplName, String[] text, int qrSize, int fontSize, String[] pathToExtraImages,
            List<Integer[]> coordinatesExtra) throws IOException, WriterException {
        String qrTemplatesPath = "./../data/qr-templates/" + tmplName + ".jpg";
        String qrCodesPath = "./../data/qr-codes/" + tmplName + ".pdf";
        create(qrTemplatesPath, "https://kwork.ru/user/eugene_lev", qrSize, qrCoords, qrCodesPath, text, textCoords, fontSize, pathToExtraImages, coordinatesExtra);
    }
}