# DonutPlus (Fabric, client-side, MC 1.21.10)

![build](https://github.com/YOUR_USER/donutplus/actions/workflows/build.yml/badge.svg)

Open the menu with **Right Shift**. Features:

- **Money tracker** – balance (from /bal), session / 24h / 7d income & spending, sell income per hour, transaction history (config/donutplus_ledger.json)
- **Auctions** – /pay-based bidding with a live HUD, winner announcement and history
- **Price watch** – scans the open container (e.g. /ah) and pings when a keyword item is at/below your per-item price; per-item price shown in tooltips
- **HUD** – balance, session profit, totem count, coords, auction timer, low-armor warning
- **Pay/TPA guard** – holds /pay above a threshold and anything to names on your watch list until you press Y
- **Ad blocker** – regex list in config
- **Command hotkeys** – 4 bindable keys, one command per press
- **Profit of the day HUD** (with optional daily goal) and **win streak counter** (regex based, configurable)
- **Payment chat colours** by tier, optional compact format
- **Popup alert** for big payments (animated, optional sound)
- **/pay confirmation screen** for big payments and watched names, with a delay on the Confirm button
- **Chat filter** for small payments and per-sender spam
- **Sky**: solid colour or rainbow sky, optional fog and cloud colour (works on every server)
- **Texture packs**: import a .zip and toggle installed packs from the UI
- **Texture editor**: replace any texture (totem, end crystal, sun, moon...) with your own PNG; stored in a generated pack called "DonutPlus Textures"
- Every feature is customisable in the menu; all keybinds are under Options > Controls > DonutPlus

## Install
Needs Minecraft 1.21.10, Fabric Loader and Fabric API. Download the jar from the Releases page into your `mods` folder.

## Build
Needs JDK 21 and Gradle 9.1+. Run `gradle build` (or generate a wrapper once with `gradle wrapper` and commit it,
then use `./gradlew build`). The jar ends up in `build/libs/`. GitHub Actions builds every push automatically.

## Releasing
`git tag v1.0.0 && git push --tags` builds the mod and attaches the jar to a GitHub Release.

## Important
The chat regexes in `donutplus.json` are best guesses. Check the real DonutSMP payment/balance/sell
messages and edit `recvRegex`, `sentRegex`, `sellRegex`, `balanceRegex` if numbers don't track.
Check DonutSMP's rules before using any client mod.
