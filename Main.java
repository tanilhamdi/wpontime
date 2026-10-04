import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


void help(){
  System.out.println("Usage: wpontime [OPTION]");
  System.out.println("-help //to see this screen.");
  System.out.println("-setup //to set up the scirpt for the first time.");
  System.out.println("-settings (-t) for time (-wp) for wallpaper //to do adjustments for your comfort.");
}

void setWallpaper(String path){
  try {
    Runtime.getRuntime().exec(new String[]{"feh", "--bg-fill", path});
  } catch (Exception e) {
    e.printStackTrace();
  }
}

void main(String[] args) throws Exception{

  Settings st = new Settings();

   if (args.length != 0) {
     if (args[0].equals("-help")){
       help();
       return;
     }else if(args[0].equals("-h")){
       help();
     }else if(args[0].equals("-setup")){
       st.setup();
     }else if(args[0].equals("-settings")){
       if(args[1].equals("-t")){

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the starting hour for the first wallpaper period (2 digits)");
        System.out.print("-> ");
        int newhourN = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the starting minute for the first wallpaper period (2 digits)");
        System.out.print("-> ");
        int newminuteN = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter the ending hour for the first wallpaper period (2 digits)");
        System.out.print("-> ");
        int newhourM = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the ending minute for the first wallpaper period (2 digits)");
        System.out.print("-> ");
        int newminuteM = Integer.parseInt(scanner.nextLine());

        List<String> hourupdatedList = st.updateHours(newhourN, newminuteN, newhourM, newminuteM);
        st.overwrite(hourupdatedList);
        scanner.close();
       }else if(args[1].equals("-wp")){
          Scanner scanner = new Scanner(System.in);
          System.out.println("Enter the path of wallpaper that you want to use night period.");
          System.out.print("-> ");
          String darkWp = scanner.nextLine();
          System.out.println("Enter the path of wallpaper that you want to use morning period.");
          System.out.print("-> ");
          String lightWp = scanner.nextLine();
          st.fst_wp_path = darkWp;
          st.scd_wp_path = lightWp;
          List<String> freshLines = st.scanFile();
          for(int i = 0; i < freshLines.size(); i++){
            System.out.println("[DEBUG] FreshLines Lines: " + freshLines.get(i));
          }
          List<String> finalLines = st.updateWPs(freshLines);
          for(int i = 0; i < finalLines.size(); i++){
            System.out.println("[DEBUG] FinalLines Lines: " + finalLines.get(i));
          }
          st.overwrite(finalLines);
        }else{
          System.out.println("Unknown option: " + args[0]);
          help();
          return;
        }
     }else{
       System.out.println("Unknown option: " + args[0]);
       help();
       return;
     }
   }

 LocalDateTime now = LocalDateTime.now();
  int nowMin    = now.getHour() * 60 + now.getMinute();
  int startMin  = st.hourN * 60 + st.minuteN;   // beginning of the period
  int endMin    = st.hourM * 60 + st.minuteM;   // ending of the period

  boolean inPeriod;
  if (startMin > endMin) {
    inPeriod = nowMin >= startMin || nowMin < endMin;
  } else {
    inPeriod = nowMin >= startMin && nowMin < endMin;
  }

  if (inPeriod) {
    setWallpaper(st.fst_wp_path);
  } else {
    setWallpaper(st.scd_wp_path);
  }
}


