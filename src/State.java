/**
 * Created by tomo on 2016/02/23.
 * ボードの回転対象、線対称を作る。
 * 正解のｘ座標y座標を回転させる。
 * bx byのneuralBoardまだ
 */
class State{
    int x;
    int y;
    int bx;
    int by;
    int blackOrWhite;
    int beforeBoard[];
    double neuralBoard[][];
    int rotateBeforeBoard[][];
    int[][] rotatePoint;
    public State(int[] beforeBoard,int blackOrWhite,int x,int y,int bx,int by){
        neuralBoard= new double[8][2*Main.BOARD_SIZE*Main.BOARD_SIZE];
        this.x=x;
        this.y=y;
        this.blackOrWhite=blackOrWhite;
        this.beforeBoard = beforeBoard;
        this.bx=bx;
        this.by=by;
        rotateBeforeBoard = new int[8][beforeBoard.length];
        rotatePoint = new int[8][4];

        //PrintPoint(x,y,bx,by);
        Rotate(beforeBoard,rotateBeforeBoard,rotatePoint,x,y,bx,by);
        //System.out.println("???????????????????");
        //PrintBoard(beforeBoard);
        for(int p=0;p<8;p++) {
            //PrintBoard(rotateBeforeBoard[p]);
            int index = 0;
            for (int k = 0; k < 2; k++) {
                for (int i = 0; i < Main.BOARD_SIZE; i++) {
                    for (int j = 0; j < Main.BOARD_SIZE; j++) {
                        index = k*Main.BOARD_SIZE*Main.BOARD_SIZE+(i * Main.BOARD_SIZE + j);
                        if (k == 0 && blackOrWhite==Move.BLACK) {
                            if (rotateBeforeBoard[p][Move.get_z((i+1),(j+1))] == 1) {
                                neuralBoard[p][index] = 1;
                            } else {
                                neuralBoard[p][index] = 0;
                            }
                        }
                        else if (k == 1 && blackOrWhite==Move.BLACK) {
                            if (rotateBeforeBoard[p][Move.get_z((i+1),(j+1))] == -1) {
                                neuralBoard[p][index] = 1;
                            } else {
                                neuralBoard[p][index] = 0;
                            }
                        }
                        else if (k == 0 && blackOrWhite==Move.WHITE) {
                            if (rotateBeforeBoard[p][Move.get_z((i+1),(j+1))] == -1) {
                                neuralBoard[p][index] = 1;
                            } else {
                                neuralBoard[p][index] = 0;
                            }
                        }
                        else if(k == 1 && blackOrWhite==Move.WHITE) {
                            if (rotateBeforeBoard[p][Move.get_z((i+1),(j+1))] == 1) {
                                neuralBoard[p][index] = 1;
                            } else {
                                neuralBoard[p][index] = 0;
                            }
                        }
                    }
                }
            }
            /*
            for (int i = 0; i < Main.BOARD_SIZE; i++) {
                for (int j = 0; j < Main.BOARD_SIZE; j++) {
                    int plusIndex = Main.BOARD_SIZE * i + j;
                    if (bx == j+1 && by == j+1) {
                        neuralBoard[p][index + plusIndex] = 1;
                    } else {
                        neuralBoard[p][index + plusIndex] = 0;
                    }
                }
            }
            */
        }
        /*
        for(int g=0;g<8;g++) {
            //System.out.println("!!!!!!!!!!!!!!!!g:"+g);
            for(int f=0;f<3;f++) {
                //System.out.println("??????????????????f:"+f);
                for (int m = 0; m < Main.BOARD_SIZE; m++) {
                    for (int n = 0; n < Main.BOARD_SIZE; n++) {
                        System.out.printf("%1.0f ", neuralBoard[g][f*Main.BOARD_SIZE*Main.BOARD_SIZE+ Main.BOARD_SIZE*m + n]);
                    }
                    System.out.println();
                }
                System.out.println();
            }
        }
        */
    }
    public void TestRotate(int[] beforeBoard){
        int[] temp = beforeBoard;
        int[][] tempBoard = new int[Main.width][Main.width];
        int[][] temp2Board = new int[Main.width][Main.width];

        //PrintBoard(beforeBoard);
        for (int i = 0; i < Main.BOARD_SIZE+2; i++) {
            for (int j = 0; j < Main.BOARD_SIZE+2; j++) {
                tempBoard[i][j] = temp[i*Main.width+j];
            }
        }
        //PrintBoard2(tempBoard);
        for (int i = 0; i < Main.width; i++) {
            for (int j = 0; j < Main.width; j++) {
                temp2Board[Main.width-1-j][i] = tempBoard[i][j];
            }
        }

        for (int i = 0; i < Main.width; i++) {
            for (int j = 0; j < Main.width; j++) {
                temp[i*Main.width+j] = temp2Board[i][j];
            }
        }

        //Move.print_board(rotateBoard[f*4+k]);
        //PrintBoard(temp);

    }
    public void Rotate(int[] beforeBoard,int[][] rotateBoard,int[][] rotatePoint,int x,int y,int bx,int by){
        int[] temp = beforeBoard;//参照渡し
        int tempX=x,tempY=y,tempBX=bx,tempBY=by;
        int fx=x,fy=y,fbx=bx,fby=by;

        for(int f=0;f<4;f++) {
            rotatePoint[f][0] =tempY ;//x
            rotatePoint[f][1] =(Main.BOARD_SIZE+1-tempX);//y
            rotatePoint[f][2] =tempBY;//bx
            rotatePoint[f][3] =(Main.BOARD_SIZE+1-tempBX);//by

            tempX = rotatePoint[f][0] ;//x
            tempY = rotatePoint[f][1] ;//y
            tempBX = rotatePoint[f][2] ;//bx
            tempBY = rotatePoint[f][3] ;//by

            //PrintPoint(rotatePoint[f][0],rotatePoint[f][1],rotatePoint[f][2],rotatePoint[f][3]);
            for (int i = 1; i <= Main.BOARD_SIZE; i++) {
                for (int j = 1; j <= Main.BOARD_SIZE; j++) {
                    rotateBoard[f][(Main.width-1-j)*Main.width+i] = temp[i*Main.width+j];
                }
            }
            //PrintBoard(rotateBoard[f]);

            for (int i = 1; i <= Main.BOARD_SIZE; i++) {
                for (int j = 1; j <= Main.BOARD_SIZE; j++) {
                    temp[i*Main.width+j] = rotateBoard[f][i*Main.width+j];
                }
            }

        }

        //線対称

        tempX =  Main.BOARD_SIZE+1-tempX;//x
        //tempY =  ;//y
        tempBX = Main.BOARD_SIZE+1-tempBX;//bx
        //tempBY = ;//by
        //PrintPoint(tempX,tempY,tempBX,tempBY);


        int[] temp2 = new int[beforeBoard.length];
        for (int i = 1; i <= Main.BOARD_SIZE; i++) {
            for (int j = 1; j <= Main.BOARD_SIZE; j++) {
                temp2[i*Main.width+j] = temp[i*Main.width+(Main.width-1-j)];
            }
        }
        //PrintBoard(temp2);
        for (int i = 1; i <= Main.BOARD_SIZE; i++) {
            for (int j = 1; j <= Main.BOARD_SIZE; j++) {
                temp[i*Main.width+j] = temp2[i*Main.width+j];
            }
        }

        for(int f=4;f<8;f++) {
            rotatePoint[f][0] =tempY ;//x
            rotatePoint[f][1] =(Main.BOARD_SIZE+1-tempX);//y
            rotatePoint[f][2] =tempBY;//bx
            rotatePoint[f][3] =(Main.BOARD_SIZE+1-tempBX);//by

            tempX = rotatePoint[f][0] ;//x
            tempY = rotatePoint[f][1] ;//y
            tempBX = rotatePoint[f][2] ;//bx
            tempBY = rotatePoint[f][3] ;//by

            //PrintPoint(rotatePoint[f][0],rotatePoint[f][1],rotatePoint[f][2],rotatePoint[f][3]);
            for (int i = 1; i <= Main.BOARD_SIZE; i++) {
                for (int j = 1; j <= Main.BOARD_SIZE; j++) {
                    rotateBoard[f][(Main.width-1-j)*Main.width+i] = temp[i*Main.width+j];
                }
            }
            //PrintBoard(rotateBoard[f]);

            for (int i = 1; i <= Main.BOARD_SIZE; i++) {
                for (int j = 1; j <= Main.BOARD_SIZE; j++) {
                    temp[i*Main.width+j] = rotateBoard[f][i*Main.width+j];
                }
            }

        }

        for(int i=0;i<8;i++){
            if(fbx==-1||fby==-1) {
                rotatePoint[i][2]=-1;
                rotatePoint[i][3]=-1;
            }
        }
        /*
        for(int p=0;p<8;p++){
            PrintPoint(rotatePoint[p][0],rotatePoint[p][1],rotatePoint[p][2],rotatePoint[p][3]);
        }
        */
    }

    public void PrintBoard(int[] board){
        for (int i = 1; i <= Main.BOARD_SIZE; i++) {
            for (int j = 1; j <= Main.BOARD_SIZE; j++) {
                    if(board[i*Main.width+j]==1){
                        System.out.printf("x");
                    }
                    else if(board[i*Main.width+j]==-1){
                        System.out.printf("o");
                    }
                    else{
                        System.out.printf("+");
                    }

            }
            System.out.println();
        }
        System.out.println();
    }
    public void PrintPoint(int x,int y,int bx,int by){
        System.out.println("x:"+x+"y:"+y+"bx:"+bx+"by:"+by);
    }
    public void PrintBoard2(int[][] board){
        for (int i = 1; i <= Main.BOARD_SIZE; i++) {
            for (int j = 1; j <= Main.BOARD_SIZE; j++) {
                if(board[i][j]==1){
                    System.out.printf("x");
                }
                else if(board[i][j]==-1){
                    System.out.printf("o");
                }
                else{
                    System.out.printf("+");
                }

            }
            System.out.println();
        }
        System.out.println();
    }

    public void PrintBR(){
        for(int i=0;i<20;i++){
            System.out.println();
        }
    }
}

