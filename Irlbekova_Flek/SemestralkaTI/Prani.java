package SemestralkaTI;

import java.util.Scanner;

public class Prani {

    public static String vypis;
    public static boolean zavrene_dvere = false;
    public static boolean PU_start = false;
    public static boolean PU_hladina_max = false;
    public static boolean PU_teplota = false;
    public static boolean hotovo = false;
    public static STAV stav = STAV.IDLE;
    public static boolean casovac = false;
    public static boolean PU_cas = false;
    public static boolean PU_hladina_min = false;
    public static int citac = 3;
    public static boolean PU_prvni_cyklus = false;
    public static boolean PU_druhy_cyklus = false;
    public static boolean prvni_cyklus = true;
    public static boolean druhy_cyklus = false;
    
    static Scanner sc = new Scanner(System.in);

    public static void start () {
        vypis = """
                Manual: Pro vstup signalu od cidel pouzijte klavesnici.
                Signály prichazi v nasledujicim poradi:
                S (start)
                Z (zavreni dveri)
                H (hladina stoupla na pozadovane maximum)
                C (teplota stoupla na pozadovanou hodnotu)
                V (casovac dosahl pozadovane hodnoty - 4x po sobe)
                L (hladina klesla na pozadovane minimum)
                P (dokoncujeme prvni cyklus)
                D (dokoncujeme druhy cyklus)
                Jsem v\s""";
        vypisStav(vypis);
        vypis = "Jsem v ";

        while (true) {
            String input = vratInput(sc);
            zjistiAkci(input);
            stavovyAutomat();
            if (hotovo == true) {
                hotovo = false;
                System.out.println ("Program skoncil uspesne.");
                VolbaProgramu.uvitani();
            }
            vypisStav(vypis);
        }
    }

    public static void stavovyAutomat() {
        switch (stav){
            case IDLE:                      {stav_0();break;}
            case START:                     {stav_1();break;}
            case NAPOUSTENI_VODY:           {stav_2();break;}
            case OHRIVANI_VODY:             {stav_3();break;}
            case MEZI_TOCENIM1:             {stav_4();break;}
            case MEZI_TOCENIM2:             {stav_5();break;}
            case VYPOUSTENI_VODY:           {stav_6();break;}
            case PRED_DALSIM_CYKLEM:        {stav_7();break;}
            default:
                vypis = "Katastroficka chyba.";
                break;
        }
    }

    public static void stav_0 (){
        if (PU_start == true && zavrene_dvere == false) {
            PU_start = false;
            stav = STAV.START;
        }else if(PU_start == true && zavrene_dvere == true) {
        	stav = STAV.NAPOUSTENI_VODY;
        }
    }

    /**
     * zde menim stavy a zaroven provadim akce
     */
    public static void stav_1 (){
        // jsou zavrene dvere?
        if (zavrene_dvere == false) {
            System.out.println ("Prosim zavrete dvere.");
        } else {
            stav = STAV.NAPOUSTENI_VODY;
            System.out.println("Otevreni napousteciho ventilu.");
        }
    }
    
    public static void stav_2 (){
        if (PU_hladina_max == true) {
            PU_hladina_max = false;
            System.out.println("Zavreni napousteciho ventilu.");
            if(prvni_cyklus == true) {
            	System.out.println("Zapnuti topne spiraly.");
            	stav = STAV.OHRIVANI_VODY;
            }else {
                stav = STAV.MEZI_TOCENIM1;
                // spustim casovac
                casovac = true;
                System.out.println("Spoustim casovac.");
                System.out.println("Zacinam tocit motorem po smeru hodinovych rucicek.");
            }
        }
    }
    
    public static void stav_3 (){
        if (PU_teplota == true) {
        	PU_teplota = false;
            System.out.println("Vypnuti topne spiraly.");
            stav = STAV.MEZI_TOCENIM1;
            // spustim casovac
            casovac = true;
            System.out.println("Spoustim casovac.");
            System.out.println("Zacinam tocit motorem po smeru hodinovych rucicek.");
        }
    }

    public static void stav_4 (){
        if (PU_cas == true){
            System.out.println("Zastavim toceni motoru.");
            stav = STAV.MEZI_TOCENIM2;
            System.out.println("Zacinam tocit mototrem proti smeru hodinovych rucicek");
            PU_cas = false;
            citac -=1;
        }
    }

    public static void stav_5 (){
        if(PU_cas == true) {
            System.out.println("Casovac dosahl pozadovaene hodnoty po druhe. Zastavim toceni motoru proti smeru hodinovych rucicek.");
            // kolikaty cyklus to je?
            if (citac != 0) {
                stav = STAV.MEZI_TOCENIM1;
                System.out.println("Zacinam tocit motorem po smeru hodinovych rucicek.");
                PU_cas = false;
                citac -= 1;
                PU_start = false;
            } else {
                // treti cyklus zdimani za nami -> prechod na vypousteni
                stav = STAV.VYPOUSTENI_VODY;
                System.out.println("Ukoncuji toceni.");
                System.out.println("Oteviram vypousteci ventil.");
                System.out.println("Spoustim cerpadlo.");
                citac = 3;
                PU_cas = false;
                // vypnu casovac
                casovac = false;
            }
        }
    }

    public static void stav_6 (){
    	if (PU_hladina_min == true) {
    		System.out.println ("Vypinam cerpadlo.");
            System.out.println ("Zaviram vypousteci ventil.");
            PU_hladina_min = false;
            stav = STAV.PRED_DALSIM_CYKLEM;
        }
    	if(!druhy_cyklus && !prvni_cyklus) {
            System.out.println("Konec tretiho cyklu.");
        }
    }

    public static void stav_7 () {
        if(PU_prvni_cyklus == true) {
        	PU_prvni_cyklus = false;
        	prvni_cyklus = false;
        	druhy_cyklus = true;
        	stav = STAV.NAPOUSTENI_VODY;
            System.out.println("Otevreni napousteciho ventilu.");
        }else if (PU_druhy_cyklus == true) {
        	System.out.println ("Vypinam cerpadlo.");
            System.out.println ("Zaviram vypousteci ventil.");
            PU_hladina_min = false;
        	PU_druhy_cyklus = false;
        	druhy_cyklus = false;
            stav = STAV.MEZI_TOCENIM1;
            // spustim casovac
            casovac = true;
            System.out.println("Spoustim casovac.");
            System.out.println("Zacinam tocit motorem po smeru hodinovych rucicek.");
        }else if (prvni_cyklus == false && druhy_cyklus == false){
        	System.out.println("Konec tretiho cyklu.");
        	System.out.println("Konec prani.");
            hotovo = true;
            stav = STAV.IDLE;
        }
    
    }


    /**
     * Prevadim jednopismenkovy vstup z klavesnice na signal od cidla a vypisuji ho.
     * Pripadne nastavuji priznaky udalosti.
     * @param akce
     */
    public static void zjistiAkci(String akce) {
        if (akce.length() > 0) {
            switch (Character.toUpperCase(akce.charAt(0))) {
                // dvere jsou zavrene
                case 'S': {
                	if(stav != STAV.IDLE ) {
                		System.out.println("Tento prikaz nelze momentalne pouzit");
                	}else {
                		PU_start = true;
                        System.out.println("Stiskli jsme start.");
                	}
                    break;
                }
                // dvere zavrene / otevrene
                case 'Z': {
                	if(stav != STAV.IDLE && stav != STAV.START) {
                		System.out.println("Tento prikaz nelze momentalne pouzit");
                	}else if (zavrene_dvere == false) {
                        zavrene_dvere = true;
                        System.out.println("Dvere jsme prave zavreli.");
                    } else {
                        zavrene_dvere = false;
                        System.out.println("Dvere jsme prave otevreli.");
                    }
                    break;
                }

                // casovac dosahl pozadovane hodnoty
                case 'V': {
                	if(stav == STAV.MEZI_TOCENIM1 || stav == STAV.MEZI_TOCENIM2) {
                		PU_cas = true;
                        System.out.println("Casovac dosahl pozadovane hodnoty.");
                	}else {
                		System.out.println("Tento prikaz nelze momentalne pouzit");
                	}
                    break;
                }

                // hladina dosahla minimalni hodnoty
                case 'L': {
                	if(stav != STAV.VYPOUSTENI_VODY) {
                		System.out.println("Tento prikaz nelze momentalne pouzit");
                	}else {
                		PU_hladina_min = true;
                        System.out.println("Hladina vody je pod minimalni pozadovanou hodnotou.");
                	}
                    break;
                }
                
                // hladina dosahla maximalni hodnoty
                case 'H': {
                	if(stav != STAV.NAPOUSTENI_VODY) {
                		System.out.println("Tento prikaz nelze momentalne pouzit");
                	}else {
                		PU_hladina_max = true;
                        System.out.println("Hladina vody je nad maximalni pozadovanou hodnotou.");
                	}
                    break;
                }
                
                // hladina dosahla maximalni hodnoty
                case 'C': {
                	if(stav != STAV.OHRIVANI_VODY) {
                		System.out.println("Tento prikaz nelze momentalne pouzit");
                	}else {
                		PU_teplota = true;
                        System.out.println("Teplota dosáhla požadované teploty.");
                	}
                    break;
                }
                
                // jedna se o konec prvniho cyklu
                case 'P': {
                	if(stav != STAV.PRED_DALSIM_CYKLEM || !prvni_cyklus && druhy_cyklus) {
                		System.out.println("Tento prikaz nelze momentalne pouzit");
                	}else {
                		PU_prvni_cyklus = true;
                		System.out.println("Konec prvniho cyklu.");
                	}
                    break;
                }
                
                // jedna se o konec druheho cyklu
                case 'D': {
                	if(stav != STAV.PRED_DALSIM_CYKLEM || !druhy_cyklus){
                		System.out.println("Tento prikaz nelze momentalne pouzit");
                	}else {
                		PU_druhy_cyklus = true;
                        System.out.println("Konec druheho cyklu.");
                	}
                    break;
                }
               

                default: {
                    System.out.println("Neplatne zadani.");
                }
            }
        }
    }

    /**
     * metoda pro vypis
     * @param vypis string, ktery ma byt vypsan
     */
    public static void vypisStav (String vypis){
        System.out.println (vypis + stav + ".");
    	System.out.println("Zadejte platnou akci:");
    }

    /**
     * vraci vstup z klavesnice
     * @param sc
     * @return
     */
    public static String vratInput(Scanner sc) {
        String akce = sc.nextLine();
        return akce;
    }
}