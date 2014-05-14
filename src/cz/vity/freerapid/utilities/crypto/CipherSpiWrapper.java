package cz.vity.freerapid.utilities.crypto;

import javax.crypto.CipherSpi;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author ntoskrnl
 */
class CipherSpiWrapper extends CipherSpi {

    private static final Method engineSetMode;
    private static final Method engineSetPadding;
    private static final Method engineGetBlockSize;
    private static final Method engineGetOutputSize;
    private static final Method engineGetIV;
    private static final Method engineGetParameters;
    private static final Method engineInit1;
    private static final Method engineInit2;
    private static final Method engineInit3;
    private static final Method engineUpdate1;
    private static final Method engineUpdate2;
    private static final Method engineUpdate3;
    private static final Method engineDoFinal1;
    private static final Method engineDoFinal2;
    private static final Method engineDoFinal3;
    private static final Method engineWrap;
    private static final Method engineUnwrap;
    private static final Method engineGetKeySize;

    static {
        try {
            engineSetMode = CipherSpi.class.getDeclaredMethod("engineSetMode", String.class);
            engineSetMode.setAccessible(true);
            engineSetPadding = CipherSpi.class.getDeclaredMethod("engineSetPadding", String.class);
            engineSetPadding.setAccessible(true);
            engineGetBlockSize = CipherSpi.class.getDeclaredMethod("engineGetBlockSize");
            engineGetBlockSize.setAccessible(true);
            engineGetOutputSize = CipherSpi.class.getDeclaredMethod("engineGetOutputSize", Integer.TYPE);
            engineGetOutputSize.setAccessible(true);
            engineGetIV = CipherSpi.class.getDeclaredMethod("engineGetIV");
            engineGetIV.setAccessible(true);
            engineGetParameters = CipherSpi.class.getDeclaredMethod("engineGetParameters");
            engineGetParameters.setAccessible(true);
            engineInit1 = CipherSpi.class.getDeclaredMethod("engineInit", Integer.TYPE, Key.class, SecureRandom.class);
            engineInit1.setAccessible(true);
            engineInit2 = CipherSpi.class.getDeclaredMethod("engineInit", Integer.TYPE, Key.class, AlgorithmParameterSpec.class, SecureRandom.class);
            engineInit2.setAccessible(true);
            engineInit3 = CipherSpi.class.getDeclaredMethod("engineInit", Integer.TYPE, Key.class, AlgorithmParameters.class, SecureRandom.class);
            engineInit3.setAccessible(true);
            engineUpdate1 = CipherSpi.class.getDeclaredMethod("engineUpdate", byte[].class, Integer.TYPE, Integer.TYPE);
            engineUpdate1.setAccessible(true);
            engineUpdate2 = CipherSpi.class.getDeclaredMethod("engineUpdate", byte[].class, Integer.TYPE, Integer.TYPE, byte[].class, Integer.TYPE);
            engineUpdate2.setAccessible(true);
            engineUpdate3 = CipherSpi.class.getDeclaredMethod("engineUpdate", ByteBuffer.class, ByteBuffer.class);
            engineUpdate3.setAccessible(true);
            engineDoFinal1 = CipherSpi.class.getDeclaredMethod("engineDoFinal", byte[].class, Integer.TYPE, Integer.TYPE);
            engineDoFinal1.setAccessible(true);
            engineDoFinal2 = CipherSpi.class.getDeclaredMethod("engineDoFinal", byte[].class, Integer.TYPE, Integer.TYPE, byte[].class, Integer.TYPE);
            engineDoFinal2.setAccessible(true);
            engineDoFinal3 = CipherSpi.class.getDeclaredMethod("engineDoFinal", ByteBuffer.class, ByteBuffer.class);
            engineDoFinal3.setAccessible(true);
            engineWrap = CipherSpi.class.getDeclaredMethod("engineWrap", Key.class);
            engineWrap.setAccessible(true);
            engineUnwrap = CipherSpi.class.getDeclaredMethod("engineUnwrap", byte[].class, String.class, Integer.TYPE);
            engineUnwrap.setAccessible(true);
            engineGetKeySize = CipherSpi.class.getDeclaredMethod("engineGetKeySize", Key.class);
            engineGetKeySize.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load CipherSpi methods", e);
        }
    }

    public static CipherSpiWrapper wrap(final CipherSpi wrapped) {
        if (wrapped instanceof CipherSpiWrapper) {
            return (CipherSpiWrapper) wrapped;
        } else {
            return new CipherSpiWrapper(wrapped);
        }
    }

    private final CipherSpi wrapped;

    private CipherSpiWrapper(final CipherSpi wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void engineSetMode(String mode) {
        try {
            engineSetMode.invoke(wrapped, mode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void engineSetPadding(String padding) {
        try {
            engineSetPadding.invoke(wrapped, padding);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineGetBlockSize() {
        try {
            return (Integer) engineGetBlockSize.invoke(wrapped);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineGetOutputSize(int inputLen) {
        try {
            return (Integer) engineGetOutputSize.invoke(wrapped, inputLen);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] engineGetIV() {
        try {
            return (byte[]) engineGetIV.invoke(wrapped);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AlgorithmParameters engineGetParameters() {
        try {
            return (AlgorithmParameters) engineGetParameters.invoke(wrapped);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void engineInit(int mode, Key key, SecureRandom random) {
        try {
            engineInit1.invoke(wrapped, mode, key, random);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void engineInit(int mode, Key key, AlgorithmParameterSpec params, SecureRandom random) {
        try {
            engineInit2.invoke(wrapped, mode, key, params, random);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void engineInit(int mode, Key key, AlgorithmParameters params, SecureRandom random) {
        try {
            engineInit3.invoke(wrapped, mode, key, params, random);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        try {
            return (byte[]) engineUpdate1.invoke(wrapped, input, inputOffset, inputLen);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) {
        try {
            return (Integer) engineUpdate2.invoke(wrapped, input, inputOffset, inputLen, output, outputOffset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineUpdate(ByteBuffer input, ByteBuffer output) {
        try {
            return (Integer) engineUpdate3.invoke(wrapped, input, output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) {
        try {
            return (byte[]) engineDoFinal1.invoke(wrapped, input, inputOffset, inputLen);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) {
        try {
            return (Integer) engineDoFinal2.invoke(wrapped, input, inputOffset, inputLen, output, outputOffset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineDoFinal(ByteBuffer input, ByteBuffer output) {
        try {
            return (Integer) engineDoFinal3.invoke(wrapped, input, output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] engineWrap(Key key) {
        try {
            return (byte[]) engineWrap.invoke(wrapped, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Key engineUnwrap(byte[] wrappedKey, String wrappedKeyAlgorithm, int wrappedKeyType) {
        try {
            return (Key) engineUnwrap.invoke(wrapped, wrappedKey, wrappedKeyAlgorithm, wrappedKeyType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineGetKeySize(Key key) {
        try {
            return (Integer) engineGetKeySize.invoke(wrapped, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
