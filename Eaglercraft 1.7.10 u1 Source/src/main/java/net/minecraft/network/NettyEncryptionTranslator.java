package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator {
	private final Cipher field_150507_a;
	private byte[] field_150505_b = new byte[0];
	private byte[] field_150506_c = new byte[0];
	private static final String __OBFID = "CL_00001237";

	protected NettyEncryptionTranslator(Cipher p_i45140_1_) {
		this.field_150507_a = p_i45140_1_;
	}

	private byte[] func_150502_a(ByteBuf p_150502_1_) {
		int var2 = p_150502_1_.readableBytes();

		if (this.field_150505_b.length < var2) {
			this.field_150505_b = new byte[var2];
		}

		p_150502_1_.readBytes(this.field_150505_b, 0, var2);
		return this.field_150505_b;
	}

	protected void func_150504_a(ByteBuf p_150504_1_, ByteBuf p_150504_2_) throws ShortBufferException {
		int var3 = p_150504_1_.readableBytes();
		byte[] var4 = this.func_150502_a(p_150504_1_);
		int var5 = this.field_150507_a.getOutputSize(var3);

		if (this.field_150506_c.length < var5) {
			this.field_150506_c = new byte[var5];
		}

		p_150504_2_.writeBytes(this.field_150506_c, 0, this.field_150507_a.update(var4, 0, var3, this.field_150506_c));
	}
}
