# EasyImageSuite
A Java library for encrypting &amp; decrypting images to/from resource files.

## Introduction

EasyImageSuite for a simple image encryption/decryption library. Store image files into customized resource files & decrypt them for later use using a key.

This project was written for personal usage, but is free to use under the MIT License.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/austinnixholm/EasyImageSuite/blob/main/LICENSE)

## Progress & Updates
Current version: **0.1.1**

This version currently supports exporting **and** importing encrypted resource files using AES-256-CBC.

Key/IV generation is also available as of December 20, 2020.

See src/test for a working *no restrictions* implementation.
Note: Export restrictions are not fully tested.

### Version 0.1.1:
- Added credential generation
- Added SUITE_ERROR error response type

### Version 0.1.0:
- Cleaned up project, refactored directories
- Changed implementation of AES-256-CBC encryption/decryption
- Implemented JUnit to the project as the testing platform
- Implemented importing of resource files
- Modified header format for resource files

### Version 0.0.1: 
- Upload project to github
- Added AES-256-CBC encryption/decryption support
- Basic implementation of exporting of resource files
  - Parses image files of the specified (or default) file extensions, with optional ignored folders/specific files
  - Exports files within the allowed size of a resource file (default or set by user)
- Added test for exporting resource files.
