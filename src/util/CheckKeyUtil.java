package util;

public class CheckKeyUtil {
    public static boolean CheckKey(){
        String storeValue = ConfigValue.readConfig("storeKey");
        String decrypt = AESHandle.Decrypt(storeValue, "chen1234567890@@");
        if (ComputerInfo.getComputerID().equals(decrypt)){
            return true;
        }
        return false;
    }
}
