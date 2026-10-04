#!/bin/bash

# 1. Check if 'feh' is installed
if ! command -v feh &> /dev/null; then
    echo "[!] Error: 'feh' is required but not installed."
    echo "[!] Please install feh first (e.g., sudo pacman -S feh or sudo apt install feh)."
    exit 1
fi

# 2. Get the current absolute path of the project
INSTALL_DIR="$(pwd)"

echo "[+] Installing wpontime..."

# 3. Compile Java files
javac Main.java Settings.java

# 4. Dynamically generate the wpontime wrapper script
cat << EOF > wpontime
#!/bin/bash
cd "$INSTALL_DIR"
java Main "\$@"
EOF

# 5. Make files executable
chmod +x wpontime
chmod +x Main.class 2>/dev/null || true

# 6. Create a symbolic link in /usr/local/bin for global usage
if [ -w /usr/local/bin ]; then
    ln -sf "$INSTALL_DIR/wpontime" /usr/local/bin/wpontime
else
    echo "[!] Root privileges required for /usr/local/bin, prompting for sudo..."
    sudo ln -sf "$INSTALL_DIR/wpontime" /usr/local/bin/wpontime
fi

echo "[SUCCESS] Installation completed! You can now use 'wpontime' from anywhere."
