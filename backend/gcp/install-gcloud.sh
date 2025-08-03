#!/bin/bash

# Google Cloud SDK Installation Script for macOS
# This script handles common SSL certificate issues

echo "Installing Google Cloud SDK..."

# Method 1: Using Homebrew (Recommended)
if command -v brew &> /dev/null; then
    echo "Homebrew detected. Installing gcloud via Homebrew..."
    brew install --cask google-cloud-sdk
    
    # Add to PATH for zsh
    echo 'source "$(brew --prefix)/share/google-cloud-sdk/path.zsh.inc"' >> ~/.zshrc
    echo 'source "$(brew --prefix)/share/google-cloud-sdk/completion.zsh.inc"' >> ~/.zshrc
    
    echo "Installation complete! Please run: source ~/.zshrc"
    exit 0
fi

# Method 2: Direct download with SSL workaround
echo "Homebrew not found. Using direct download method..."

# Create a temporary directory
TEMP_DIR=$(mktemp -d)
cd $TEMP_DIR

# Download using curl with SSL certificate verification disabled (temporary workaround)
echo "Downloading Google Cloud SDK..."
curl -Lo google-cloud-sdk.tar.gz https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-cli-darwin-arm.tar.gz

# Extract
tar -xf google-cloud-sdk.tar.gz

# Move to home directory
mv google-cloud-sdk ~/

# Install
cd ~/google-cloud-sdk
./install.sh --quiet

# Add to PATH
echo '# The next line updates PATH for the Google Cloud SDK.' >> ~/.zshrc
echo 'if [ -f "$HOME/google-cloud-sdk/path.zsh.inc" ]; then . "$HOME/google-cloud-sdk/path.zsh.inc"; fi' >> ~/.zshrc
echo '# The next line enables shell command completion for gcloud.' >> ~/.zshrc
echo 'if [ -f "$HOME/google-cloud-sdk/completion.zsh.inc" ]; then . "$HOME/google-cloud-sdk/completion.zsh.inc"; fi' >> ~/.zshrc

# Clean up
rm -rf $TEMP_DIR

echo "Installation complete!"
echo "Please run: source ~/.zshrc"
echo "Then run: gcloud init"