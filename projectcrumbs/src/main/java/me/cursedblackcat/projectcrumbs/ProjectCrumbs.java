package me.cursedblackcat.projectcrumbs;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.javacord.api.AccountType;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;

public class ProjectCrumbs extends Application {	
	DiscordApi api;
	TextChannel lastActiveChannel;
	
	ArrayList<StickerPack> stickerPacks = new ArrayList<StickerPack>();
	
	StickerPack activePack;
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public static void main(String[] args){
		launch(args);
	}

	private DiscordApi login(String token) throws Exception {
		token = token.replaceAll("\"", "");

		api =  new DiscordApiBuilder().setAccountType(AccountType.CLIENT).setToken(token).login().join();

		api.addMessageCreateListener(event -> {
			try {
				if (event.getMessageAuthor().asUser().get().equals(api.getYourself())) {
					lastActiveChannel = event.getChannel();
				}
			} catch (NoSuchElementException e) {
				//do nothing, the author was a webhook
			}
		});

		System.out.println("Logged in as " + api.getYourself().getDiscriminatedName());

		return api;
	}

	private Scene loginScreen() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("Login");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label tokenLabel = new Label("Discord Token:");
		grid.add(tokenLabel, 0, 1);

		TextField tokenTextField = new TextField();
		grid.add(tokenTextField, 1, 1);

		Button btn = new Button("Log in");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 2);

		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					api = login(tokenTextField.getText());
					new Alert(AlertType.INFORMATION, "Successfully logged in as " + api.getYourself().getDiscriminatedName()).showAndWait();
				} catch (Exception e) {
					new Alert(AlertType.ERROR, "Invalid token.").showAndWait();
					tokenTextField.setText("");
				}
			}
		});

		return new Scene(grid, 300, 275);
	}
	
	private Scene stickerScene() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));
		
		HBox stickerPackSelector = new HBox();
		stickerPackSelector.setMaxHeight(50);
		
		ArrayList<Button> stickerPackButtons = new ArrayList<Button>();
		
		for (StickerPack s : stickerPacks) {
			Button btn = new Button();
			btn.setMinSize(50, 50);
			btn.setMaxSize(50, 50);
			ImageView v = new ImageView(s.getIcon());
			v.setFitHeight(50);
			v.setFitWidth(50);
			
			BackgroundImage bImage = new BackgroundImage(s.getIcon(), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(btn.getWidth(), btn.getHeight(), true, true, true, false));

		    Background backGround = new Background(bImage);
		    btn.setBackground(backGround);
			
			btn.setGraphic(v);
			stickerPackButtons.add(btn);
			stickerPackSelector.getChildren().add(btn);
		}
		
		grid.add(stickerPackSelector, 0, 0, 10, 1); //span 10 columns, assuming 100px per sticker and 1000px width
		
		return new Scene(grid, 1000, 500);
	}
	
	private void loadStickerPacks() {
	    try {
			File jarDirectory = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			System.out.println("Current directory: " + jarDirectory.getPath());
			File stickerDirectory = new File(jarDirectory.getPath() + "\\stickers");
			stickerDirectory.mkdirs();
			File[] packDirectories = stickerDirectory.listFiles(File::isDirectory);
			
			for (File f : packDirectories) {
				ArrayList<Image> stickers = new ArrayList<Image>();
				
				for (File i : f.listFiles(File::isFile)) {
					stickers.add(new Image(i.toURI().toString()));
				}
				
				Image icon = stickers.get(0);
				System.out.println("Sticker pack detected: " + f.getName() + " with " + stickers.size() + " stickers");
				stickerPacks.add(new StickerPack(f.getName(),stickers, icon));
			}
			
		} catch (URISyntaxException e) {
			new Alert(AlertType.ERROR, "Error occurred when fetching sticker pack list: URISyntaxException. See console for stack trace.").showAndWait();
		} catch (NullPointerException e) {
			new Alert(AlertType.ERROR, "Error occurred when fetching sticker pack list: NullPointerException. See console for stack trace.").showAndWait();
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			new Alert(AlertType.ERROR, "Error occurred when fetching sticker pack list: ArrayIndexOutOfBoundsException. See console for stack trace.").showAndWait();
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) {
		loadStickerPacks();
		activePack = stickerPacks.get(0);
		
		primaryStage.setTitle("Project Crumbs");
		//TODO set it back to this primaryStage.setScene(loginScreen());
		primaryStage.setScene(stickerScene());
		primaryStage.show();
	}
}
