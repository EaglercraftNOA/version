package net.minecraft.network;

import io.netty.buffer.ByteBuf;

import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptingDecoder {
	private final NettyEncryptionTranslator field_150509_a;
	private static final String __OBFID = "CL_00001238";

	public NettyEncryptingDecoder(Cipher p_i45141_1_) {
		this.field_150509_a = new NettyEncryptionTranslator(p_i45141_1_);
	}

}
