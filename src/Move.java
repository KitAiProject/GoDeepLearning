import java.io.*;
import java.util.ArrayList;

/**
 * Created by tomo on 2016/02/23.
 * bx by は前に打った手の座標　パスの時はbx=-1,by=-1
 */
class Move{
    ArrayList moveList;
    String kifuStr;
    public static final int BLACK = 1;
    public static final int WHITE = -1;
    int board[] = {
            3,3,3,3,3,3,3,3,3,3,3,
            3,0,0,0,0,0,0,0,0,0,3,
            3,0,0,0,0,0,0,0,0,0,3,
            3,0,0,0,0,0,0,0,0,0,3,
            3,0,0,0,0,0,0,0,0,0,3,
            3,0,0,0,0,0,0,0,0,0,3,
            3,0,0,0,0,0,0,0,0,0,3,
            3,0,0,0,0,0,0,0,0,0,3,
            3,0,0,0,0,0,0,0,0,0,3,
            3,0,0,0,0,0,0,0,0,0,3,
            3,3,3,3,3,3,3,3,3,3,3
    };
    int ko_z=0;
    int[] dir4 = {+1, +Main.width, -1, -Main.width};
    int[] dir8 = { +1,+Main.width,-1,-Main.width,  +1+Main.width, +Main.width-1, -1-Main.width, -Main.width+1 };
    Move(int num,int boardSize,String str){
        moveList = new ArrayList();

        try{
            File file = new File("src/CosumiSGF/"+str+"/" + String.valueOf(num)+".sgf");
            BufferedReader br = new BufferedReader(new FileReader(file));

            kifuStr = br.readLine();
            System.out.println(kifuStr);
            br.close();
        }catch(FileNotFoundException e){
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int bx=-1;
        int by=-1;
        for(int i=0;i<kifuStr.length()-4;i++){
            if(i==0){continue;}
            int blackOrWhite;
            int x=-1,y=-1;

            String charBW = String.valueOf(kifuStr.charAt(i));
            String semicolon = String.valueOf(kifuStr.charAt(i-1));
            String parentheses = String.valueOf(kifuStr.charAt(i+1));
            String charXY =kifuStr.substring(i+2,i+4);
            if(";".equals(semicolon)&&"[".equals(parentheses)){
                if("B".equals(charBW)){
                    blackOrWhite = BLACK;
                }
                else if("W".equals(charBW)){
                    blackOrWhite = WHITE;
                }
                else{
                    return;
                }
                byte[] asciiCodes;
                //String sampleString = xChar;
                try {
                    asciiCodes = charXY.getBytes("US-ASCII");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                for (int k = 0; k < asciiCodes.length; k++) {
                    if(asciiCodes[k]==116){
                        break;
                    }
                    for(int j=0;j<boardSize;j++){
                        if(asciiCodes[k]==97+j){
                            if(k==0){
                                x=j+1;
                            }
                            else if(k==1){
                                y=j+1;
                            }
                            else{
                                return;
                            }
                        }
                    }

                }

                int[] beforeBoard;
                beforeBoard = board.clone();

                if(x==-1||y==-1){
                    break;
                }
                if(PutStone(x,y,blackOrWhite,1)==0) {
                    //ルール通りに打ち、グローバル変数boardを渡して記録する。
                    //System.out.println(" !!!!!!!!!!!!!!!!!!!"+bx+""+by);
                    moveList.add(new State(beforeBoard, blackOrWhite, x, y,bx,by));
                }
                bx=x;
                by=y;
            }
        }
    }

    int flip_color(int col)
    {
        return -col;
    }
    int check_board[] = new int[Main.boardMax];
    void count_liberty_sub(int tz, int color)
    {
        int z,i;

        check_board[tz] = 1;     // search flag
        (stone)++;            // number of stone
        for (i=0;i<4;i++) {
            z = tz + dir4[i];
            if ( check_board[z]==1) continue;
            if ( board[z] == 0 ) {
                check_board[z] = 1;
                (liberty)++;      // number of liberty
            }
            if ( board[z] == color ) count_liberty_sub(z, color);
        }
    }

    void count_liberty(int tz)
    {
        int i;
        liberty = stone = 0;
        for (i=0;i<Main.boardMax;i++) check_board[i] = 0;
        count_liberty_sub(tz, board[tz]);
    }

    int gLiberty,gStone;
    int liberty,stone;
    int PutStone(int x,int y, int color, int fill_eye_err) {
        int[][] around = new int[4][3];
        int un_col = flip_color(color);
        int space = 0;
        int wall = 0;
        int mycol_safe = 0;
        int capture_sum = 0;
        int ko_maybe = 0;
        int i;
        int tz=get_z(x,y);
        if (tz == 0) {
            ko_z = 0;
            return 0;
        }  // pass

        // count 4 neighbor's liberty and stones.

            for (i = 0; i < 4; i++) {
                int z, c;
                //int liberty, stone;
                around[i][0] = around[i][1] = around[i][2] = 0;
                z = tz + dir4[i];

                c = board[z];  // color
                //System.out.println("c:"+c+"z:"+z);
                if (c == 0) space++;
                if (c == 3) wall++;
                if (c == 0 || c == 3) continue;
                count_liberty(z);
                around[i][0] = this.liberty;
                around[i][1] = this.stone;
                around[i][2] = c;
                if (c == un_col && liberty == 1) {
                    capture_sum += stone;
                    ko_maybe = z;
                }
                if (c == color && liberty >= 2) mycol_safe++;
            }


        if (capture_sum == 0 && space == 0 && mycol_safe == 0) return 1; // suicide
        if (tz == ko_z) return 2; // ko
        if (wall + mycol_safe == 4 && fill_eye_err != 0) return 3; // eye
        if (board[tz] != 0) return 4;

        for (i = 0; i < 4; i++) {
            int lib = around[i][0];
            int c = around[i][2];
            if (c == un_col && lib == 1 && board[tz + dir4[i]] != 0) {
                take_stone(tz + dir4[i], un_col);
            }
        }

        board[tz] = color;

        count_liberty(tz);
        if (capture_sum == 1 && gStone == 1 && gLiberty == 1) ko_z = ko_maybe;
        else ko_z = 0;

        //print_board(this.board);

        return 0;
    }

    public static void print_board(int[] board)
    {
        String[] str = {".", "X", "O", "#"};
        for (int y=0;y<Main.BOARD_SIZE;y++) {
            for (int x=0;x<Main.BOARD_SIZE;x++) {
                if(board[get_z(x + 1, y + 1)]==1) {
                    System.out.printf("%s", str[1]);
                }
                else if(board[get_z(x + 1, y + 1)]==-1){
                    System.out.printf("%s", str[2]);
                }
                else if(board[get_z(x + 1, y + 1)]==0){
                    System.out.printf(".");
                }
                else{
                    System.out.printf("#");
                }
            }
            System.out.printf("\n");
        }
        /*
        for(int i=1;i<=Main.BOARD_SIZE;i++){
            for(int j=1;j<=Main.BOARD_SIZE;i++){
                if(board[Main.BOARD_SIZE*i+j]==1) {
                    System.out.printf("x");
                }
                else if(board[Main.BOARD_SIZE*i+j]==-1){
                    System.out.printf("o");
                }
                else if(board[Main.BOARD_SIZE*i+j]==0){
                    System.out.printf(".");
                }
            }
            System.out.println();
        }
        */
    }

    void take_stone(int tz,int color)
    {
        int z,i;

        board[tz] = 0;
        for (i=0;i<4;i++) {
            z = tz + dir4[i];
            if ( board[z] == color ) take_stone(z,color);
        }
    }

    static int get_z(int x,int y)
    {
        return y*Main.width + x;  // 1<= x <=9, 1<= y <=9
    }
    /*
    static int getZ(int x,int y){
        return x*Main.width + y;
    }
    */
}
