/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.notifications;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG.EnumBadgePriority;
import net.minecraft.util.text.ITextComponent;

public class NotificationBadge {
	
	public final ServerNotificationManager mgr;
	public final EaglercraftUUID badgeUUID;
	public final ITextComponent bodyComponent;
	public final ITextComponent titleComponent;
	public final ITextComponent sourceComponent;
	public final long clientTimestamp;
	public final long serverTimestamp;
	public final boolean silent;
	public final EnumBadgePriority priority;
	public final NotificationIcon mainIcon;
	public final NotificationIcon titleIcon;
	public final int hideAfterSec;
	public final int expireAfterSec;
	public final int backgroundColor;
	public final int bodyTxtColor;
	public final int titleTxtColor;
	public final int sourceTxtColor;

	protected long hideAtMillis = -1l;
	protected boolean unreadFlag = true;
	protected boolean unreadFlagRender = true;
	
	protected NotificationBadge(ServerNotificationManager mgr, EaglercraftUUID badgeUUID, ITextComponent bodyComponent,
			ITextComponent titleComponent, ITextComponent sourceComponent, long clientTimestamp, long serverTimestamp,
			boolean silent, EnumBadgePriority priority, NotificationIcon mainIcon, NotificationIcon titleIcon,
			int hideAfterSec, int expireAfterSec, int backgroundColor, int bodyTxtColor, int titleTxtColor,
			int sourceTxtColor) {
		this.mgr = mgr;
		this.badgeUUID = badgeUUID;
		this.bodyComponent = bodyComponent;
		this.titleComponent = titleComponent;
		this.sourceComponent = sourceComponent;
		this.clientTimestamp = clientTimestamp;
		this.serverTimestamp = serverTimestamp;
		this.silent = silent;
		this.priority = priority;
		this.mainIcon = mainIcon;
		this.titleIcon = titleIcon;
		this.hideAfterSec = hideAfterSec;
		this.expireAfterSec = expireAfterSec;
		this.backgroundColor = backgroundColor;
		this.bodyTxtColor = bodyTxtColor;
		this.titleTxtColor = titleTxtColor;
		this.sourceTxtColor = sourceTxtColor;
	}
	
	protected void incrIconRefcounts() {
		if(mainIcon != null) {
			mainIcon.retain();
		}
		if(titleIcon != null) {
			titleIcon.retain();
		}
	}
	
	protected void decrIconRefcounts() {
		if(mainIcon != null) {
			mainIcon.release();
		}
		if(titleIcon != null) {
			titleIcon.release();
		}
	}

	protected void deleteGLTexture() {
	}

	public void hideNotif() {
		if(hideAtMillis == -1l) {
			markRead();
			unreadFlagRender = false;
			hideAtMillis = EagRuntime.steadyTimeMillis();
		}
	}

	public void removeNotif() {
		mgr.removeNotifFromActiveList(badgeUUID);
	}

	public void markRead() {
		if(unreadFlag) {
			unreadFlag = false;
			--mgr.unreadCounter;
		}
	}

	public ITextComponent getBodyProfanityFilter() {
		return bodyComponent;
	}

	public ITextComponent getTitleProfanityFilter() {
		return titleComponent;
	}

	public ITextComponent getSourceProfanityFilter() {
		return sourceComponent;
	}

}
