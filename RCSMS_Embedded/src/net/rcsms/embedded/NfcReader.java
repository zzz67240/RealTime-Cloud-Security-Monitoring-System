package net.rcsms.embedded;

import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

/**
 * NFC資料讀取類別
 * 
 * @author macdidi5
 */
public class NfcReader {
    
    // 通知讀取到資料的物件
    private final NfcCallBack nfcCallBack;
    
    // 結束
    private boolean exit;
    
    // 讀卡機物件
    private CardTerminal cardTerminal;
    
    // 是否顯示除錯訊息
    private static final boolean VERBOSE = false;
    
    /**
     * 建立NFC資料讀取物件
     * 
     * @param nfcCallBack   通知讀取到資料的物件
     */
    public NfcReader(NfcCallBack nfcCallBack) {
        this.nfcCallBack = nfcCallBack;
    }
    
    /**
     * 初始化與啟動NFC資料讀取服務
     */
    public void init() {
        verbose("NfcReader init...");
        
        // 取得讀卡機控制務件
        TerminalFactory terminalFactory = 
                TerminalFactory.getDefault();
        
        try {
            // 取得已連接的讀卡機
            List<CardTerminal> cardTerminalList = 
                    terminalFactory.terminals().list();
            
            // 如果有已連接的讀卡機
            if (cardTerminalList.size() > 0) {
                verbose("Card reader detected.");
                // 取得第一個讀卡機物件
                cardTerminal = cardTerminalList.get(0);
                // 啟動服務
                start();
            }
            else {
                System.out.println("No cardreader is detected...");
            }
        }
        catch (CardException e) {
            System.out.println("============ " + e.toString());
        }
    }
    
    /**
     * 停止服務
     */
    public void stop() {
        exit = true;
    }
    
    private void start() {
        // 建立與啟動服務
        new Thread() {
            @Override
            public void run() {
                verbose("NfcReader start...");
                
                while (!exit) {
                    try {
                        // 等候NFC裝置或卡片接近
                        cardTerminal.waitForCardPresent(0);
                        verbose("Inserted card...");
                        
                        // 讀取資料
                        handleCard(cardTerminal);
                        
                        // 等候NFC裝置或卡片移開
                        cardTerminal.waitForCardAbsent(0);
                        verbose("Removed card...");
                    }
                    catch (CardException e) {
                        System.out.println("============ " + e.toString());
                    }
                }
            }
        }.start();
    }
    
    private void handleCard(CardTerminal cardTerminal) {
        verbose("NfcReader handleCard...");
        
        try {
            // 連接NFC裝置或卡片
            Card card = cardTerminal.connect("*");
            
            try {
                // 取得NFC裝置或卡片的連線，準備交換APDU資訊
                CardChannel channel = card.getBasicChannel();
                
                // 讀取UID的指令
                byte[] address = new byte[]
                        {(byte) 0xff, (byte) 0xca, 0, 0, 0};
                
                // 建立讀取UID的命令物件
                CommandAPDU command = new CommandAPDU(address);
                
                // 傳送指令並取得回應物件
                ResponseAPDU response = channel.transmit(command);
                
                // 讀取資料
                byte[] data = response.getData();
                
                // 把讀取的位元轉換為字串
                String result = byteArrayToString(data);
                
                // 通知讀取到資料
                nfcCallBack.notify(result);
            } 
            catch (IllegalArgumentException e) {
                verbose("Card errror!  Touch again!");
            }            
            catch (CardException e) {
                System.out.println("============ " + e.toString());
            }

        } 
        catch (CardException e) {
            System.out.println("Couldn't read card, try again...");
            delay(100);
        }
    }
    
    public static String byteArrayToString(byte[] data) {
        String result = "";
        
        for (byte b : data) {
            result = result + String.format("%02X", b);
        }
        
        return result;
    }
    
    /**
     * 轉換字串為位元陣列
     * 
     * @param s 轉換的字串
     * @return  轉換後的位元陣列
     */
    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }        
    
    /**
     * 顯示除錯訊息
     * 
     * @param message
     */
    public void verbose(String message) {
        if (VERBOSE) {
            System.out.println(message);
        }
    }
    
    /**
     * 暫停指定的時間（毫秒、1000分之一秒）
     * 
     * @param ms
     */
    public static void delay(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            System.out.println("============ " + e.toString());
        }        
    }
    
    /**
     * 通知資料讀取
     */
    public interface NfcCallBack {

        /**
         * 通知資料讀取
         * 
         * @param data  讀取的資料
         */
        public void notify(String data);
    }
}
