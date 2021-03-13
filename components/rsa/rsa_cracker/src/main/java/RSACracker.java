import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class RSACracker {
    public static final RSACracker instance = new RSACracker();

    public Port port;

    public RSACracker(){
        port = new Port();
    }

    public BigInteger innerCrack(BigInteger e, BigInteger n, BigInteger cipher){
        try {
            BigInteger p, q, d;
            List<BigInteger> factorList = factorize(n);

            if (factorList.size() != 2) {
                throw new RSACrackerException("cannot determine factors p and q");
            }

            p = factorList.get(0);
            q = factorList.get(1);
            BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
            d = e.modInverse(phi);
            return cipher.modPow(d, n);
        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    public List<BigInteger> factorize(BigInteger n) {
        BigInteger two = BigInteger.valueOf(2);
        List<BigInteger> factorList = new LinkedList<>();

        if (n.compareTo(two) < 0) {
            throw new IllegalArgumentException("must be greater than one");
        }

        while (n.mod(two).equals(BigInteger.ZERO)) {
            factorList.add(two);
            n = n.divide(two);
        }

        if (n.compareTo(BigInteger.ONE) > 0) {
            BigInteger factor = BigInteger.valueOf(3);
            while (factor.multiply(factor).compareTo(n) <= 0) {
                if (n.mod(factor).equals(BigInteger.ZERO)) {
                    factorList.add(factor);
                    n = n.divide(factor);
                } else {
                    factor = factor.add(two);
                }
            }
            factorList.add(n);
        }

        return factorList;
    }

    public static RSACracker getInstance(){
        return instance;
    }

    public class Port implements IRSACracker{
        public BigInteger crack(BigInteger e, BigInteger n, BigInteger cipher){
            return innerCrack(e, n, cipher);
        }
    }

}