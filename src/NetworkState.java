import java.util.Random;

/**
 * Created by tomohiro on 2016/02/12.
 */
public class NetworkState {
    int layerNum;
    public NetworkObject[] layer;
    double[][][] data;
    int midUnitNum;
    int outputNum;
    double[][] answer;
    double ALPHA = 0.05;
    double ERROR_MIN =1;
    static double[] outnum;
    Kifu kifuObj;


    public NetworkState(Kifu kifuObj,int layerNum, int midUnitNum) {
        answer = new double[Main.BOARD_SIZE*Main.BOARD_SIZE][Main.BOARD_SIZE*Main.BOARD_SIZE];
        for (int i = 0; i < Main.BOARD_SIZE; i++) {
            for(int l=0;l<Main.BOARD_SIZE;l++) {
                for(int j=0;j<Main.BOARD_SIZE*Main.BOARD_SIZE;j++){
                    answer[i * Main.BOARD_SIZE + l][j] = 0;
                }
                answer[i * Main.BOARD_SIZE + l][i*Main.BOARD_SIZE+l] = 1;
            }
        }

        this.kifuObj = kifuObj;
        this.layerNum = layerNum;
        this.midUnitNum = midUnitNum;
        this.outputNum = answer[0].length;

        outnum = new double[outputNum];
        layer = new NetworkObject[Main.LAYERNUM];
        layer[0] = new NetworkObject(((State)((Move)kifuObj.kifuInfo.kifuListBlack.get(0)).moveList.get(0)).neuralBoard[0].length, midUnitNum);
        layer[1] = new NetworkObject(midUnitNum, midUnitNum);
        layer[2] = new NetworkObject(midUnitNum, outputNum);

    }

    public void calc(double[] data,boolean flag){
        for(int n=0;n<layer[0].node.length;n++){
            layer[0].node[n]=0;
            for(int j=0;j<data.length;j++){
                layer[0].node[n]+=data[j]*layer[0].weightObj[n].weight[j];
            }
            //layer[0].e[n]=layer[0].node[n];
            layer[0].node[n]=sigmoid(layer[0].node[n]);
        }

        for(int i=1;i<layerNum;i++) {
            for (int n = 0; n < layer[i].node.length; n++) {
                layer[i].node[n] = 0;
                for (int j = 0; j < layer[i-1].node.length; j++) {
                    layer[i].node[n] += layer[i-1].node[j] * layer[i].weightObj[n].weight[j];
                }

                //if(i!=layerNum-1){
                    layer[i].node[n] = sigmoid(layer[i].node[n]);
                //}
            }
        }

        if(flag==true){
            for(int i=0;i<Main.BOARD_SIZE;i++){
                for(int j=0;j<Main.BOARD_SIZE;j++){
                    outnum[i*Main.BOARD_SIZE+j]=layer[layerNum-1].node[i*Main.BOARD_SIZE+j];
                    System.out.printf("%4.4f ", outnum[i*Main.BOARD_SIZE+j]*100);
                }
                System.out.println();
            }
        }
    }

    void BackPropagationCalc() {

        for(;;) {
            double e=0;
            System.out.printf("ループ");
            for (int s = 0; s < Main.SAMPLE; s++) {
                for (int l=0;l< ((Move) kifuObj.kifuInfo.kifuListBlack.get(s)).moveList.size();l++) {
                    for (int g = 0; g < 8; g++) {
                        if (((State) ((Move) kifuObj.kifuInfo.kifuListBlack.get(s)).moveList.get(l)).blackOrWhite == 1) {
                            double[] ansArray = answer[Main.BOARD_SIZE * (((State) ((Move) kifuObj.kifuInfo.kifuListBlack.get(s)).moveList.get(l)).x - 1) +
                                    ((State) ((Move) kifuObj.kifuInfo.kifuListBlack.get(s)).moveList.get(l)).y - 1];

                            calc(((State) ((Move) kifuObj.kifuInfo.kifuListBlack.get(s)).moveList.get(l)).neuralBoard[g], false);
                            e += BackPropagation(((State) ((Move) kifuObj.kifuInfo.kifuListBlack.get(s)).moveList.get(l)).neuralBoard[g], ansArray);
                        }
                    }
                }
                System.out.println("切り替わり");
                for (int l=0;l< ((Move) kifuObj.kifuInfo.kifuListWhite.get(s)).moveList.size();l++) {
                    for(int g=0;g<8;g++) {
                        if(((State) ((Move) kifuObj.kifuInfo.kifuListWhite.get(s)).moveList.get(l)).blackOrWhite==-1){
                            double[] ansArray = answer[Main.BOARD_SIZE * (((State) ((Move) kifuObj.kifuInfo.kifuListWhite.get(s)).moveList.get(l)).x-1) +
                                ((State) ((Move) kifuObj.kifuInfo.kifuListWhite.get(s)).moveList.get(l)).y-1];
                            calc(((State) ((Move) kifuObj.kifuInfo.kifuListWhite.get(s)).moveList.get(l)).neuralBoard[g], false);
                            e += BackPropagation(((State) ((Move) kifuObj.kifuInfo.kifuListWhite.get(s)).moveList.get(l)).neuralBoard[g], ansArray);
                        }
                    }
                }
            }
            System.out.println(e);
            if(ERROR_MIN>e){break;}
            /*
            for(int j=0;j<layer[layerNum-2].weightObj.length;j++) {
                System.out.print(" " + layer[layerNum-1].weightObj[0].weight[j]);
            }
            */

            Test test = new Test(Main.state);
            System.out.println();
        }
    }

    public double BackPropagation(double[] data,double[] answer) {
        double e;

        for(int j=0;j<layer[layerNum-2].node.length;j++){
            for(int i=0;i<layer[layerNum-1].node.length;i++){
                layer[layerNum-1].weightObj[i].weight[j] -= ALPHA*(layer[layerNum-1].node[i]-answer[i])*layer[layerNum-1].node[i]*(1-layer[layerNum-1].node[i])*layer[layerNum-2].node[j];
                //System.out.println(layer[layerNum-1].weightObj[i].weight[j]);
            }
        }

        double[][] arrayCenter = new double[layer[layerNum-3].node.length][layer[layerNum-2].node.length];
        for(int i=0;i<layer[layerNum-3].node.length;i++){//入力側
            for(int j=0;j<layer[layerNum-2].node.length;j++) {//中間層
                double temp = 0;
                for (int k = 0; k < layer[layerNum - 1].node.length; k++) {//出力層
                    temp += (layer[layerNum - 1].node[k] - answer[k]) * layer[layerNum - 1].node[k] * (1 - layer[layerNum - 1].node[k]) * layer[layerNum - 1].weightObj[k].weight[j];
                }
                //System.out.printf("%4.8f",temp);
                arrayCenter[i][j]=temp*layer[layerNum - 2].node[j]*(1-layer[layerNum - 2].node[j]);
                //System.out.printf("%4.8f",arrayCenter[i][j]);
                layer[layerNum-2].weightObj[j].weight[i]-=ALPHA*arrayCenter[i][j]*layer[layerNum-3].node[i];
                //System.out.println(layer[layerNum-2].weightObj[i].weight[j]);
            }
        }

        for(int h=0;h<data.length;h++) {//入力層
            for (int i = 0; i < layer[layerNum - 3].node.length; i++) {//入力側　中間層
                double temp=0;
                for (int j = 0; j < layer[layerNum - 2].node.length; j++) {//中間層
                    temp+=arrayCenter[i][j]*layer[layerNum-2].weightObj[j].weight[i];
                }
                layer[layerNum-3].weightObj[i].weight[h]-=ALPHA*temp*layer[layerNum-3].node[i]*(1-layer[layerNum-3].node[i])*data[h];
                //System.out.println(layer[layerNum-2].weightObj[i].weight[h]);
            }
        }

        e = Error(answer);
        //System.out.print("BackPropEnd");
        //System.out.printf("%10.10f\n",e);

        return e;
    }
    public double sigmoid(double x){
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public double Error(double teach[]) {
        double e = 0.0;
        for (int i = 0; i < answer[0].length; i++) {
            //System.out.print(teach[i]);
            //System.out.print(layer[layerNum-1].node[i]);
            //System.out.println(Math.pow(teach[i] - layer[layerNum-1].node[i], 2.0));
            e += Math.pow(teach[i] - layer[layerNum-1].node[i], 2.0);
        }
        //System.out.println();
        e /= answer[0].length;
        //System.out.println(e);
        return e;
    }
}

class NetworkObject {
    int unitNum;
    double[] node;
    double[] delta;
    double[] e;
    eachWeight[] weightObj;

    NetworkObject(int inputUnitNum, int outputUnitNum) {
        this.unitNum=outputUnitNum;
        node = new double[unitNum];
        weightObj = new eachWeight[unitNum];
        delta = new double[unitNum];
        for (int i = 0; i < unitNum; i++) {
            weightObj[i] = new eachWeight(inputUnitNum);
        }
    }
}
class eachWeight{
    int inputUnitNum;
    double[] weight;
    eachWeight(int inputUnitNum) {
        this.inputUnitNum=inputUnitNum;
        weight = new double[inputUnitNum];
        Random rnd = new Random();
        for(int i=0;i<weight.length;i++){
            double num =rnd.nextDouble();
            weight[i]=(num-0.5)*0.01;
        }
    }
}
