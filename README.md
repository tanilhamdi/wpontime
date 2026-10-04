# wpontime ⏱️🖼️

`wpontime` is a lightweight, time-aware wallpaper switcher designed specifically for Linux environments (tested with X11 / window managers like AwesomeWM). It dynamically changes your desktop wallpaper based on configured time periods using `feh`.

---

## Prerequisites

Before installing, ensure you have the following dependencies installed on your system:
- **Java (JDK):** Required to compile and run the application.
- **feh:** A lightweight image viewer used as the wallpaper backend.

On Arch Linux, you can install feh via:
```bash
sudo pacman -S feh
```
On Ubuntu/Debian:
```bash
sudo apt install feh
```

---

## Features

- **Time-Based Switching:** Automatically applies different wallpapers depending on the current time segment defined in your configuration.
- **Global CLI Command:** Installs as a seamless system-wide command (`wpontime`).
- **Zero-Dependency Core:** Written cleanly in Java with modular architecture (`Main.java`, `Settings.java`).
- **Automated Installer:** Includes a smart `install.sh` script that handles compilation, wrapper generation, dependency checking, and path linking automatically.

---

## Installation

Clone the repository and run the automated installer script:

```bash
git clone https://github.com/tanilhamdi/wpontime.git
cd wpontime
./install.sh
```

---

## Usage

Once installed, you can use `wpontime` from anywhere in your terminal:

```bash
wpontime          # Triggers the wallpaper check and switch based on time
wpontime -setup   # Run initial setup / configuration guide
wpontime -help    # Display help information and usage options
```

---

## Configuration

The application reads time blocks and image paths from `wpcfg/config.txt`. Ensure your paths and time intervals are properly structured inside this file.

---

## Automation / Background Usage

If you want `wpontime` to run automatically in the background (e.g., changing your wallpaper periodically without manual execution), you can use one of the following methods:

### 1. Using Cron (Simple and Universal)
Open your crontab configuration:
```bash
crontab -e
```
Add the following line to run the tool every hour:
```bash
0 * * * * /usr/local/bin/wpontime
```

### 2. Using Systemd User Timer (For Systemd-based Linux Distributions)
Create a service file at `~/.config/systemd/user/wpontime.service`:
```ini
[Unit]
Description=wpontime automatic wallpaper switcher

[Service]
Type=oneshot
ExecStart=/usr/local/bin/wpontime
```

Then, create a corresponding timer file at `~/.config/systemd/user/wpontime.timer`:
```ini
[Timer]
OnBootSec=1min
OnUnitActiveSec=30min
Persistent=true

[Install]
WantedBy=timers.target
```

Enable and start the timer with:
```bash
systemctl --user enable --now wpontime.timer
```

---

## License

Distributed under the MIT License. See `LICENSE` for more information.
