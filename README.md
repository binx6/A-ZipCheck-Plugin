# A-ZipCheck-Plugin

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
- Adler32计算: 计算Adler32值，以16进制格式返回（v1.1起）
- ZIP伪加密修复: 改自ZipCenOp，用于修复zip伪加密问题，不保证100%修复，如果修复后压缩包本身有问题而文件没问题，你可以用mt复制出来重新打包嘛，mt本身也能忽略一部分伪加密（v1.2起）

## Usage

- [Zip4j](https://github.com/srikanth-lingala/zip4j)
- [ZipCenOp](https://github.com/442048209as/ZipCenOp)

## Examples

The operation is actually very simple. Open the text editor, paste the complete path inside, long press this path and choose translate and select the plugin. Do not operate in private directories! You should first copy to the public directory!
- Example: /storage/emulated/0/Download/Test.zip

操作其实很简单，打开文本编辑器，将完整路径粘贴在内部，长按它使用翻译并选择该插件即可。请勿在私有目录操作！应先复制到公共目录！
- 示例: /storage/emulated/0/Download/Test.zip

## FAQ

问：为什么有的文件使用“损坏检测”结果无误，而其它功能相继报错呢？
- 答：首先这跟实现原理有关，“损坏检测”是不经过严格的zip api检查的，目地是为了最大限度的读取内部情况，换而言之，即使zip不标准或动过魔数的情况下，“损坏检测”也可能还会继续生效。其次，结果无误不一定代表真的没有任何问题，也可能找不到Central Dir呢，因为它没有经过打开检查只是进行内部检查。总之，请不要把它当作ZArchiver Pro中的“Test”功能来使用！目前还比较简陋没能实现到那种程度，仅供初步参考。

问：为什么不使用Java Nio File Api？
- 答：不是不想用，而是受限于MT的classes.jar，以及考虑到Android8以下的用户，虽然可以自己魔改替换掉，但为了大部分用户兼容着想，还是不用。

问：我可以用于zip/apk外的文件吗？
- 答：完全不建议，此插件专为zip定制，而且大概率不会对其它类型压缩文件起效。

问：我该怎么为这个插件贡献我的代码才算合理？
- 答：您可以在MT官方插件编写准则的范围内，自由发挥您的才能。但是请不要用来搞恶意操作，这是不道德的。还有，请您pull之前先测试代码准确无误，这样不会浪费彼此的时间，谢谢。

问：为什么选用Zip4j呢？有更好的方案啊
- 答：还是那句话，受限。例如Apache commons compress，过不了编译，或许早期版本可以过编译，但考虑到一些CVE影响等等，还是用Zip4j以及Java Api，兼容范围够广，也不算太老旧。

问：为什么不直接上架“插件中心”呢？
- 答：插件中心说实话，审核到猴年马月，不如直接开源，供大家学习交流。

问：你是MT作者Bin吗？
- 答：我不是。请不要误解。Bin是这位[→](https://github.com/L-JINBIN)。

## License

Licensed under the Anti 996 License, Version 1.0 (the "License");

you may not use this "A-ZipCheck-Plugin" project except in compliance with the License.

You may obtain a copy of the License at

https://github.com/996icu/996.ICU/blob/master/LICENSE
