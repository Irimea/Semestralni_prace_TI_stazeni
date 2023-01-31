package SemestralkaTI;
import java.util.Scanner;

public class VolbaProgramu {
    public static void main (String[] args) {
        uvitani();
    }

    public static void uvitani () {
        while (true) {
            System.out.println("Vitejte v pracce. Prosim vyberte program stitknutim tlacitka 0-3." + "\n"
                    + "0 = ukonceni programu" + "\n"
                    + "1 = odstredovani" + "\n"
                    + "2 = prani" + "\n"
                    + "3 = prani s predpirkou" + "\n");

            Scanner sc = new Scanner(System.in);

            try {
                int program = Integer.parseInt(sc.nextLine());

                if (program == 1) {
                    Odstredovani.start();
                } else if (program == 2) {
                    Prani.start();
                } else if (program == 3) {
                    Prani_s_predpirkou.start();
                } else if (program == 0) {
                    System.out.println("Program bude ukoncen.");
                    System.exit(0);
                } else {
                    System.out.println("Zvolili jste neplatny program!");
                }

            } catch (Exception e) {
                System.out.println("Zvolili jste neplatny program.");
            }
        }
    }
}
