package cz.vity.freerapid.utilities.crypto;

import javax.crypto.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author ntoskrnl
 */
final class CipherSpiWrapper extends CipherSpi {

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
            engineGetOutputSize = CipherSpi.class.getDeclaredMethod("engineGetOutputSize", int.class);
            engineGetOutputSize.setAccessible(true);
            engineGetIV = CipherSpi.class.getDeclaredMethod("engineGetIV");
            engineGetIV.setAccessible(true);
            engineGetParameters = CipherSpi.class.getDeclaredMethod("engineGetParameters");
            engineGetParameters.setAccessible(true);
            engineInit1 = CipherSpi.class.getDeclaredMethod("engineInit", int.class, Key.class, SecureRandom.class);
            engineInit1.setAccessible(true);
            engineInit2 = CipherSpi.class.getDeclaredMethod("engineInit", int.class, Key.class, AlgorithmParameterSpec.class, SecureRandom.class);
            engineInit2.setAccessible(true);
            engineInit3 = CipherSpi.class.getDeclaredMethod("engineInit", int.class, Key.class, AlgorithmParameters.class, SecureRandom.class);
            engineInit3.setAccessible(true);
            engineUpdate1 = CipherSpi.class.getDeclaredMethod("engineUpdate", byte[].class, int.class, int.class);
            engineUpdate1.setAccessible(true);
            engineUpdate2 = CipherSpi.class.getDeclaredMethod("engineUpdate", byte[].class, int.class, int.class, byte[].class, int.class);
            engineUpdate2.setAccessible(true);
            engineUpdate3 = CipherSpi.class.getDeclaredMethod("engineUpdate", ByteBuffer.class, ByteBuffer.class);
            engineUpdate3.setAccessible(true);
            engineDoFinal1 = CipherSpi.class.getDeclaredMethod("engineDoFinal", byte[].class, int.class, int.class);
            engineDoFinal1.setAccessible(true);
            engineDoFinal2 = CipherSpi.class.getDeclaredMethod("engineDoFinal", byte[].class, int.class, int.class, byte[].class, int.class);
            engineDoFinal2.setAccessible(true);
            engineDoFinal3 = CipherSpi.class.getDeclaredMethod("engineDoFinal", ByteBuffer.class, ByteBuffer.class);
            engineDoFinal3.setAccessible(true);
            engineWrap = CipherSpi.class.getDeclaredMethod("engineWrap", Key.class);
            engineWrap.setAccessible(true);
            engineUnwrap = CipherSpi.class.getDeclaredMethod("engineUnwrap", byte[].class, String.class, int.class);
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
        if (wrapped == null) throw new NullPointerException();
        this.wrapped = wrapped;
    }

    @Override
    public void engineSetMode(final String mode) throws NoSuchAlgorithmException {
        try {
            engineSetMode.invoke(wrapped, mode);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof NoSuchAlgorithmException) {
                throw (NoSuchAlgorithmException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void engineSetPadding(final String padding) throws NoSuchPaddingException {
        try {
            engineSetPadding.invoke(wrapped, padding);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof NoSuchPaddingException) {
                throw (NoSuchPaddingException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineGetBlockSize() {
        try {
            return (Integer) engineGetBlockSize.invoke(wrapped);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineGetOutputSize(int inputLen) {
        try {
            return (Integer) engineGetOutputSize.invoke(wrapped, inputLen);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] engineGetIV() {
        try {
            return (byte[]) engineGetIV.invoke(wrapped);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AlgorithmParameters engineGetParameters() {
        try {
            return (AlgorithmParameters) engineGetParameters.invoke(wrapped);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void engineInit(final int mode, final Key key, final SecureRandom random) throws InvalidKeyException {
        try {
            engineInit1.invoke(wrapped, mode, key, random);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof InvalidKeyException) {
                throw (InvalidKeyException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void engineInit(final int mode, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        try {
            engineInit2.invoke(wrapped, mode, key, params, random);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof InvalidKeyException) {
                throw (InvalidKeyException) cause;
            } else if (cause instanceof InvalidAlgorithmParameterException) {
                throw (InvalidAlgorithmParameterException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void engineInit(final int mode, final Key key, final AlgorithmParameters params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        try {
            engineInit3.invoke(wrapped, mode, key, params, random);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof InvalidKeyException) {
                throw (InvalidKeyException) cause;
            } else if (cause instanceof InvalidAlgorithmParameterException) {
                throw (InvalidAlgorithmParameterException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] engineUpdate(final byte[] input, final int inputOffset, final int inputLen) {
        try {
            return (byte[]) engineUpdate1.invoke(wrapped, input, inputOffset, inputLen);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineUpdate(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException {
        try {
            return (Integer) engineUpdate2.invoke(wrapped, input, inputOffset, inputLen, output, outputOffset);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof ShortBufferException) {
                throw (ShortBufferException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineUpdate(final ByteBuffer input, final ByteBuffer output) throws ShortBufferException {
        try {
            return (Integer) engineUpdate3.invoke(wrapped, input, output);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof ShortBufferException) {
                throw (ShortBufferException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] engineDoFinal(final byte[] input, final int inputOffset, final int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        try {
            return (byte[]) engineDoFinal1.invoke(wrapped, input, inputOffset, inputLen);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IllegalBlockSizeException) {
                throw (IllegalBlockSizeException) cause;
            } else if (cause instanceof BadPaddingException) {
                throw (BadPaddingException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineDoFinal(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        try {
            return (Integer) engineDoFinal2.invoke(wrapped, input, inputOffset, inputLen, output, outputOffset);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof ShortBufferException) {
                throw (ShortBufferException) cause;
            } else if (cause instanceof IllegalBlockSizeException) {
                throw (IllegalBlockSizeException) cause;
            } else if (cause instanceof BadPaddingException) {
                throw (BadPaddingException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineDoFinal(final ByteBuffer input, final ByteBuffer output) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        try {
            return (Integer) engineDoFinal3.invoke(wrapped, input, output);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof ShortBufferException) {
                throw (ShortBufferException) cause;
            } else if (cause instanceof IllegalBlockSizeException) {
                throw (IllegalBlockSizeException) cause;
            } else if (cause instanceof BadPaddingException) {
                throw (BadPaddingException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        try {
            return (byte[]) engineWrap.invoke(wrapped, key);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IllegalBlockSizeException) {
                throw (IllegalBlockSizeException) cause;
            } else if (cause instanceof InvalidKeyException) {
                throw (InvalidKeyException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Key engineUnwrap(final byte[] wrappedKey, final String wrappedKeyAlgorithm, final int wrappedKeyType) throws InvalidKeyException, NoSuchAlgorithmException {
        try {
            return (Key) engineUnwrap.invoke(wrapped, wrappedKey, wrappedKeyAlgorithm, wrappedKeyType);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof InvalidKeyException) {
                throw (InvalidKeyException) cause;
            } else if (cause instanceof NoSuchAlgorithmException) {
                throw (NoSuchAlgorithmException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int engineGetKeySize(final Key key) throws InvalidKeyException {
        try {
            return (Integer) engineGetKeySize.invoke(wrapped, key);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof InvalidKeyException) {
                throw (InvalidKeyException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(final Object obj) {
        return wrapped.equals(obj);
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }

}
