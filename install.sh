#!/bin/bash

# 1. Get the current absolute path of the project
INSTALL_DIR="$(pwd)"

echo "[+] Installing wpontime..."

# 2. Compile Java files
javac Main.java Settings.java

# 3. Dynamically generate the wpontime wrapper script
# (cd "$INSTALL_DIR" ensures relative paths like wpcfg/config.txt are always found correctly)
cat << EOF > wpontime
#!/bin/bash
cd "$INSTALL_DIR"
java Main "\$@"
EOF

# 4. Make files executable
chmod +x wpontime
chmod +x Main.class 2>/dev/null || true

# 5. Create a symbolic link in /usr/local/bin for global usage
if [ -w /usr/local/bin ]; then
    ln -sf "$INSTALL_DIR/wpontime" /usr/local/bin/wpontime
else
    echo "[!] Root privileges required for /usr/local/bin, prompting for sudo..."
    sudo ln -sf "$INSTALL_DIR/wpontime" /usr/local/bin/wpontime
fi

echo "[SUCCESS] Installation completed! You can now use 'wpontime' from anywhere."
