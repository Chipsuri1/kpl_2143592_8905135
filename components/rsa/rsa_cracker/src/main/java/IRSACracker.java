import java.math.BigInteger;

public interface IRSACracker {
    BigInteger crack(BigInteger e, BigInteger n, BigInteger cipher);
}
