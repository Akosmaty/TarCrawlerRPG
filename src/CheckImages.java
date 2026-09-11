import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;

public class CheckImages {
    public static void main(String[] args) throws Exception {
        String[] foes = {"foes/pip-kielichy", "foes/pip-miecze", "foes/court-jopek", "foes/court-rycerz"};
        for (String f : foes) {
            File file = new File("assets/" + f + ".png");
            if (!file.exists()) { System.out.println(f + ": MISSING"); continue; }
            BufferedImage img = ImageIO.read(file);
            int transparent = 0, opaque = 0;
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int a = (img.getRGB(x, y) >> 24) & 255;
                    if (a < 200) transparent++; else opaque++;
                }
            }
            System.out.println(f + ": " + img.getWidth() + "x" + img.getHeight() + " transparent=" + transparent + " opaque=" + opaque);
        }
        // Check sheet-b hero B portraits  
        BufferedImage sb = ImageIO.read(new File("assets/heroes/sheet-b.png"));
        for (int row = 0; row < 5; row++) {
            int x0 = 0, y0 = row * 128;
            int transparent = 0, opaque = 0;
            for (int y = y0; y < y0 + 128; y++) {
                for (int x = x0; x < x0 + 128; x++) {
                    int a = (sb.getRGB(x, y) >> 24) & 255;
                    if (a < 200) transparent++; else opaque++;
                }
            }
            System.out.println("sheet-b row" + row + ": transparent=" + transparent + " opaque=" + opaque);
        }
        // Check all/1-4 heros portraits
        BufferedImage ah = ImageIO.read(new File("assets/all/1-4 heros.png"));
        for (int row = 0; row < 4; row++) {
            int x0 = 0, y0 = row * 128;
            int transparent = 0, opaque = 0;
            for (int y = y0; y < y0 + 128; y++) {
                for (int x = x0; x < x0 + 128; x++) {
                    int a = (ah.getRGB(x, y) >> 24) & 255;
                    if (a < 200) transparent++; else opaque++;
                }
            }
            System.out.println("1-4 heros row" + row + ": transparent=" + transparent + " opaque=" + opaque);
        }
    }
}