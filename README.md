# ZipCheck-Plugin

- 100% Pure Java（纯Java）
- 100% Never Network（完全本地实现）
- Pr Welcome.（欢迎贡献，让插件变得更好）

## Features

- 损坏检测: 用于初步判断内部文件问题
- 读取测试: 利用流式读取检查zip是否存在问题
- 深度探测: 多线程耗时操作，更全面的信息
- CRC校验测试: 通过校验CRC来探测是否有损坏情况
- 整体标基测试: 通过读取文件尾部来判断是否zip
- Bomb检测: 用于检测是否为zip炸弹，默认最高仅允许1GB大小和20000个文件

## Usage

- [Zip4j](https://github.com/srikanth-lingala/zip4j)

## Examples

The operation is actually very simple. Open the text editor, paste the complete path inside, long press this path and choose translate and select the plugin. Do not operate in private directories! You should first copy to the public directory!
- Example: /storage/emulated/0/Download/Test.zip

操作其实很简单，打开文本编辑器，将完整路径粘贴在内部，长按它使用翻译并选择该插件即可。请勿在私有目录操作！应先复制到公共目录！
- 示例: /storage/emulated/0/Download/Test.zip

## FAQ

问：为什么不使用Java Nio Api？
- 答：不是不想用，而是受限于MT的classes.jar，以及考虑到Android8以下的用户，虽然可以自己魔改替换掉，但为了大部分用户兼容着想，还是不用。

问：我可以用于zip/apk外的文件吗？
- 答：完全不建议，此插件专为zip定制，而且大概率不会对其它类型压缩文件起效。

问：我该怎么为这个插件贡献我的代码才算合理？
- 答：您可以在MT官方插件编写准则的范围内，自由发挥您的才能。但是请不要用来搞恶意操作，这是不道德的。

问：为什么选用Zip4j呢？有更好的方案啊
- 答：还是那句话，受限。例如Apache commons compress，过不了编译，或许早期版本可以过编译，但考虑到一些CVE影响等等，还是用Zip4j以及Java Api，兼容范围够广，也不算太老旧。

## License

Licensed under the Anti 996 License, Version 1.0 (the "License");

you may not use this "ZipCheck-Plugin" project except in compliance with the License.

You may obtain a copy of the License at

https://github.com/996icu/996.ICU/blob/master/LICENSE
