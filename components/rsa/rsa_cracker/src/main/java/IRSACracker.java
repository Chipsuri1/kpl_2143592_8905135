import java.math.BigInteger;

public interface IRSACracker {
    BigInteger decrypt(BigInteger e, BigInteger n, BigInteger cipher);
}
