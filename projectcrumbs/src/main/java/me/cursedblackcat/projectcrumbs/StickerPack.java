package me.cursedblackcat.projectcrumbs;

import java.util.ArrayList;

import javafx.scene.image.Image;

public class StickerPack {
	String packName;
	ArrayList<Image>  stickers;
	Image icon;
	
	public StickerPack(String name, ArrayList<Image>  stickers, Image icon) {
		packName = name;
		this.stickers = stickers;
		this.icon = icon;
	}
	
	public String getPackName() {
		return packName;
	}
	
	public ArrayList<Image> getStickers() {
		return stickers;
	}
	
	public Image getIcon() {
		return icon;
	}
}
