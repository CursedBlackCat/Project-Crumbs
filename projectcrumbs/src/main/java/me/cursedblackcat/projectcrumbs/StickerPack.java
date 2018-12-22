package me.cursedblackcat.projectcrumbs;

import java.io.File;
import java.util.ArrayList;

import javafx.scene.image.Image;

public class StickerPack {
	String packName;
	ArrayList<Image>  stickers;
	ArrayList<File> stickersFile;
	Image icon;
	
	public StickerPack(String name, ArrayList<Image> stickers, ArrayList<File> stickersF, Image icon) {
		packName = name;
		this.stickers = stickers;
		stickersFile = stickersF;
		this.icon = icon;
	}
	
	public String getPackName() {
		return packName;
	}
	
	public ArrayList<Image> getStickers() {
		return stickers;
	}
	
	public ArrayList<File> getStickersFile() {
		return stickersFile;
	}
	
	public Image getIcon() {
		return icon;
	}
	
	public File getFile(Image i) {
		return stickersFile.get(stickers.indexOf(i));
	}
}
