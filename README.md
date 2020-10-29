# EasyImageSuite
A Java library for encrypting &amp; decrypting images to/from resource files.

## Introduction

EasyImageSuite for a simple image encryption/decryption library. Store image files into customized resource files & decrypt them for later use using a key.

This project was written for personal usage, but is free to use under the MIT License.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/austinnixholm/EasyImageSuite/blob/main/LICENSE)

## Progress & Updates
Current version: **0.0.1**

This version currently only supports exporting resource files as of October 25th, 2020. 

Importing of resource files will be added in short time.

### Version 0.0.1: 
- Upload project to github
- Added AES-256-CBC encryption/decryption support
- Basic implementation of exporting of resource files
  - Parses image files of the specified (or default) file extensions, with optional ignored folders/specific files
  - Exports files within the allowed size of a resource file (default or set by user)
- Added test for exporting resource files.
