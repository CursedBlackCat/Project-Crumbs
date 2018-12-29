package me.cursedblackcat.projectcrumbs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import org.javacord.api.AccountType;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;

public class ProjectCrumbs extends Application {
	DiscordApi api;
	TextChannel lastActiveChannel;

	ArrayList<StickerPack> stickerPacks = new ArrayList<StickerPack>();

	StickerPack activePack;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	Image clearImage;
	File token;

	public static void main(String[] args){
		launch(args);
	}

	private DiscordApi login(String token) throws LoginException {
		token = token.replaceAll("\"", "");

		api =  new DiscordApiBuilder().setAccountType(AccountType.CLIENT).setToken(token).login().join();

		api.addMessageCreateListener(event -> {
			try {
				if (event.getMessageAuthor().asUser().get().equals(api.getYourself())) {
					lastActiveChannel = event.getChannel();
					System.out.println("Active channel set to " + event.getChannel().toString());
				}
			} catch (NoSuchElementException e) {
				//do nothing, the author was a webhook
			}
		});

		System.out.println("Logged in as " + api.getYourself().getDiscriminatedName());

		return api;
	}

	private Scene loginScreen(Stage stage) {
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
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(token));
			tokenTextField.setText(reader.readLine());
		} catch (FileNotFoundException e1) {
			new Alert(AlertType.WARNING, "Could not find token config file.").showAndWait();
		} catch (IOException e) {
			new Alert(AlertType.WARNING, "Could not read token config file.").showAndWait();
		} catch (Exception e) {
			new Alert(AlertType.WARNING, "Unknown error occurred while reading token config file.").showAndWait();
		}
		

		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					api = login(tokenTextField.getText());
					new Alert(AlertType.INFORMATION, "Successfully logged in as " + api.getYourself().getDiscriminatedName()).showAndWait();
					
					BufferedWriter writer = new BufferedWriter(new FileWriter(token, false));
					writer.write(tokenTextField.getText());
					writer.close();
					
					stage.setScene(stickerScene(stage));
				} catch (IOException e) {
					new Alert(AlertType.ERROR, "Couldn't write token to config (IOException). See console for stack trace.").showAndWait();
					e.printStackTrace();
				} catch (LoginException e) {
					new Alert(AlertType.ERROR, "Invalid token.").showAndWait();
					tokenTextField.setText("");
				} catch (NullPointerException e) {
					new Alert(AlertType.ERROR, "Couldn't find image background (NullPointerException). See console for stack trace.").showAndWait();
					e.printStackTrace();
				}
			}
		});

		return new Scene(grid, 300, 275);
	}

	private Scene stickerScene(Stage stage) {
		GridPane parentGrid = new GridPane();
		parentGrid.setAlignment(Pos.TOP_LEFT);
		parentGrid.setHgap(10);
		parentGrid.setVgap(10);
		
		GridPane stickerPackGrid = new GridPane();
		
		ScrollPane stickerPackScroll = new ScrollPane();
		stickerPackScroll.setMinHeight(65);
		stickerPackScroll.setStyle("-fx-background-color:transparent;");
		stickerPackScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		stickerPackScroll.setContent(stickerPackGrid);
		
		GridPane stickerGrid = new GridPane();
		stickerGrid.setAlignment(Pos.TOP_LEFT);
		stickerGrid.setHgap(10);
		stickerGrid.setVgap(10);
		stickerGrid.setPadding(new Insets(5, 5, 5, 5));
		
		ScrollPane stickerScroll = new ScrollPane();
		stickerScroll.setFitToWidth(true);
		stickerScroll.setStyle("-fx-background-color:transparent;");
		stickerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		stickerScroll.setContent(stickerGrid);
		
		parentGrid.add(stickerPackScroll, 0, 0);
		parentGrid.add(stickerScroll, 0, 1);

		//Make the top bar a sticker selector
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

			btn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					activePack = stickerPacks.get(stickerPackButtons.indexOf(btn));
					stage.setScene(stickerScene(stage));
				}
			});

			BackgroundImage bImage = new BackgroundImage(s.getIcon(), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(btn.getWidth(), btn.getHeight(), true, true, true, false));

			Background backGround = new Background(bImage);
			btn.setBackground(backGround);

			btn.setGraphic(v);
			stickerPackButtons.add(btn);
			stickerPackSelector.getChildren().add(btn);
		}

		stickerPackGrid.add(stickerPackSelector, 0, 0, 10, 1); //span 10 columns

		//Make a grid of sticker icons, 100x100px each, for the currently active pack
		int row = 1;
		int column = 0;
		for (Image i : activePack.getStickers()) {
			Button btn = new Button();
			btn.setMinSize(100, 100);
			btn.setMaxSize(100, 100);
			ImageView v = new ImageView (i);
			v.setFitHeight(100);
			v.setFitWidth(100);

			BackgroundImage bImage = new BackgroundImage(clearImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(btn.getWidth(), btn.getHeight(), true, true, true, false));

			Background backGround = new Background(bImage);
			btn.setBackground(backGround);

			btn.setGraphic(v);

			btn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					lastActiveChannel.sendMessage(activePack.getFile(v.getImage()));
				}
			});

			stickerGrid.add(btn, column, row);
			column++;
			if (column > 9) {
				row++;
				column = 0;
			}
		}

		return new Scene(parentGrid, 1100, 500);
	}

	private void loadStickerPacks() {
		try {
			File jarDirectory = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			//System.out.println("Current directory: " + jarDirectory.getPath());
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
				stickerPacks.add(new StickerPack(f.getName(),stickers, new ArrayList<File>(Arrays.asList(f.listFiles(File::isFile))), icon));
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
	
	private void loadConfig() {
		try {
			File jarDirectory = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
			File configDirectory = new File(jarDirectory.getPath() + "\\config");
			configDirectory.mkdirs();
			
			token = new File(configDirectory.getPath() + "\\token.pcdss");
			token.createNewFile();

			clearImage = new Image(this.getClass().getResource("clear.png").toURI().toString());
			
		} catch (URISyntaxException e) {
			new Alert(AlertType.ERROR, "Error occurred when loading config: URISyntaxException. See console for stack trace.").showAndWait();
		} catch (NullPointerException e) {
			//Config file not found, this will occur on first launch
		} catch (ArrayIndexOutOfBoundsException e) {
			new Alert(AlertType.ERROR, "Error occurred when loading config: ArrayIndexOutOfBoundsException. See console for stack trace.").showAndWait();
			e.printStackTrace();
		} catch (IOException e) {
			new Alert(AlertType.ERROR, "Error occurred when loading config: IOException. See console for stack trace.").showAndWait();
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) {
		loadStickerPacks();
		loadConfig();
		try {
			activePack = stickerPacks.get(0);
		} catch (IndexOutOfBoundsException e) {
			new Alert(AlertType.INFORMATION, "No stickers found. Please add sticker pack subfolders in the stickers folder and ensure that there are no empty sticker packs.").showAndWait();
			e.printStackTrace();
			System.exit(0);
		}

		primaryStage.setTitle("Project Crumbs v1.2.0");
		primaryStage.setScene(loginScreen(primaryStage));
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
		
		primaryStage.show();
	}
}