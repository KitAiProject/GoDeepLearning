import java.util.ArrayList;

/**
 * Created by tomo on 2016/02/23.
 */
class KifuInformation{
    ArrayList kifuListBlack;
    ArrayList kifuListWhite;
    public KifuInformation(int sample,int boardSize){
        kifuListBlack = new ArrayList();
        kifuListWhite = new ArrayList();
        for(int i=1;i<=sample;i++){
            kifuListBlack.add(new Move(i,boardSize,"black"));
            kifuListWhite.add(new Move(i,boardSize,"white"));
        }
    }
}