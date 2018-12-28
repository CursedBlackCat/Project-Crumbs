# Project Crumbs Discord Sticker Sender
The Project Crumbs Discord Sticker Sender (PCDSS) is a project by CursedBlackCat#7801, which is intended to facilitate the sending of LINE sticker images on Discord.

## Important Disclaimer
This program signs in to your Discord user account via the Discord API. Discord officially does not allow selfbots, so this program falls into a grey area in terms of Discord's Terms of Service. The program author will not be held responsible if your Discord account is terminated for using this program. **Use this program at your own risk!!**

## Getting Started
Pick up the [latest release JAR](https://github.com/CursedBlackCat/Project-Crumbs/releases/latest), and save it in a directory that is convenient for you. On first launch, the program will create a folder called `stickers` and warn you that there are no stickers. You can create sticker packs by making folders in the `stickers` folder, and adding images to the folders.

Please note that the program will not work if you have an empty sticker pack (i.e. an empty folder inside the `stickers` folder that the program created.

Relaunch the program after that, and you will then be prompted to login with a Discord token. You can obtain your token as follows:

1. Visit [discordapp.com](https://discordapp.com/)
2. Sign in, if you haven't already (if the button at the top right says "Open" instead of "Login", you are already logged in)
3. After logging in, return to Discord's home page
4. Open the developer console (usually Ctrl-Shift-I or F12)
5. Open the "Network" tab
6. Type "/api" (without the quotation marks) into the "Filter" textbox
7. Click on any of the requests, then on the right, click "Headers"
8. Look for a request that has "Authorization" under "Request headers". The string after "Authorization" is your token. Be sure to copy the full token. You might find it easier to click the button that says "HAR" at the far right, then "Save All as HAR", and open the resulting file as a JSON file from which you can copy the Authorization token.

Before continuing, **please be extremely careful with your token.** Anyone with your token will be able to use your Discord account. This program does not steal tokens, and you can look at the code to see for yourself. All this program does is store your token on your own machine so that you don't need to copy and paste it every time. You should always be **extremely careful not to share your token with ANYONE**.

If you feel safe to continue, copy and paste that token into the program, and the program will sign into Discord. **The program may freeze or become unresponsive while logging in; this is normal**. Signing in should normally take no longer than a minute or two.

If you've done everything right up until this point, the program should then display a message saying it has signed in, and it will load your stickers packs and display them. You can change sticker packs by clicking on the small icons in the top bar, and you can send a sticker by clicking on a sticker. **By default, the program will send a sticker to the channel in which you last sent a message**.
