/*
 * Copyright (c) 2022-2024 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.sp.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiSlider2 extends GuiButton {

	public float sliderValue = 1.0F;
	public float sliderMax = 1.0F;
	public boolean dragging = false;

	public GuiSlider2(int buttonId, int x, int y, int widthIn, int heightIn, float sliderValue, float sliderMax) {
		super(buttonId, x, y, widthIn, heightIn, null);
		this.sliderValue = sliderValue;
		this.sliderMax = sliderMax;
		this.displayString = updateDisplayString();
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		return 0;
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double mouseDX, double mouseDY) {
		if(this.visible && this.dragging) {
			setSliderValue(mouseX);
		}
	}

	@Override
	protected void renderBg(Minecraft mc, int mouseX, int mouseY) {
		if(this.enabled) {
			this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
			this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		setSliderValue(mouseX);
		this.dragging = true;
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		this.dragging = false;
	}

	private void setSliderValue(double mouseX) {
		float oldValue = this.sliderValue;
		this.sliderValue = (float)(mouseX - (double)(this.x + 4)) / (float)(this.width - 8);
		if(this.sliderValue < 0.0F) {
			this.sliderValue = 0.0F;
		}
		if(this.sliderValue > 1.0F) {
			this.sliderValue = 1.0F;
		}
		if(oldValue != this.sliderValue) {
			onChange();
		}
		this.displayString = updateDisplayString();
	}

	protected String updateDisplayString() {
		return (int)(this.sliderValue * this.sliderMax * 100.0F) + "%";
	}

	protected void onChange() {
	}

	public boolean isSliderTouchEvents() {
		return true;
	}

}
