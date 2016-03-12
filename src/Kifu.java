

/**
 * Created by tomo on 2016/02/22.
 */
public class Kifu {
    KifuInformation kifuInfo;

    int boardSize;

    public Kifu(int sample,int boardSize){

        kifuInfo =new KifuInformation(sample,boardSize);

    }
    public void InitializeBoard(double[] board){
        for(int i=0;i<boardSize;i++){
            for(int j=0;j<boardSize;j++){
                board[boardSize*i+j]=0;
            }
        }
    }
    public void View(double[] BeforeBoard,int blackOrWhite,int x,int y){
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(x==j&&y==i){
                    if(blackOrWhite==1) {
                        BeforeBoard[boardSize*i+j]=1;
                    }
                    else if(blackOrWhite==-1){
                        BeforeBoard[boardSize*i+j]=-1;
                    }
                }
            }
        }
    }
}



