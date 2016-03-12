public class Main {

    /**
     * @author Tomohiro Ueno
     * @version 1
     */

    public static int BOARD_SIZE  = 9;
    public static int SAMPLE = 10;

    static final int LAYERNUM = 3;
    static final int MIDUNIT_NUM = 4*Main.BOARD_SIZE*Main.BOARD_SIZE;
    static int width;
    static int boardMax;
    public static NetworkState state;
    Kifu kifu;
    Main() {
        message("Roading Data [ " + SAMPLE + " ]");
        width = BOARD_SIZE+2;
        boardMax = width*width;
        kifu = new Kifu(SAMPLE,BOARD_SIZE);
        //Test test = new Test(kifu);

        state = new NetworkState(kifu, LAYERNUM, MIDUNIT_NUM);
        state.BackPropagationCalc();
        Test test = new Test(state);
        message("Learning END");
    }

    public static void message(String str) {
        System.out.println(" " + str);
    }

    public static void main(String[] args) {
        new Main();
    }
}
