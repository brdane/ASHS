
public class ConsoleTesting
{
    public static void main(String... args) throws Exception
    {
        String old = "Very-Important-Text.";
        String thekey = PlusMinusEncrypt.generateKey();

        String encrypted = PlusMinusEncrypt.encrypt(old, thekey);
        String decrypted = PlusMinusEncrypt.decrypt(encrypted, thekey);

        System.out.printf("Original: %s \n Encrypted: %s \n Decrypted: %s".formatted(old,encrypted,decrypted));
    }

}
