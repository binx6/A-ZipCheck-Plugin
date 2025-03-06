package me.zipcheck.plugin;

import bin.mt.plugin.api.MTPluginContext;
import bin.mt.plugin.api.preference.PluginPreference;

public class evaluate implements PluginPreference {

    @Override
    public void onBuild(MTPluginContext context, Builder builder) {
        builder.addHeader("About");

        builder.addText("Brief")
                .summary("一个适用于校验Zip完整性的插件\n希望它能成为辅助您的好帮手。\n100% Pure Java\n100% Never Network");

        builder.addText("Operation")
                .summary("The operation is actually very simple. Open the text editor, paste the complete path inside, long press this path and choose translate and select the plugin. Do not operate in private directories! You should first copy to the public directory! \nExample: /storage/emulated/0/Download/Test.zip");

        builder.addText("Explanation")
                .summary("免责声明：\n不论何种情形作者都不对由于操作问题、使用不当所造成的任何损失负责任\n不论是可预见的或是不可预见的，即使作者已被告知这种可能性\n使用者对本插件的使用即表明同意承担下载和使用本插件的一切风险。");

        builder.addText("Notice")
                .summary("当前版本部分功能不支持处理带密码的Zip文件\n还有请勿作用于非zip/apk/jar文件\n以及尽量不要用于超大zip/apk/jar文件\n如果您的zip文件有损坏，可以尝试使用rarlab修复\nhttps://www.rarlab.com/download.htm")
                .url("https://www.rarlab.com/download.htm");

        builder.addHeader("Special thanks");
        
        builder.addText("Zip4j")
                .summary("srikanth-lingala")
                .url("https://github.com/srikanth-lingala/zip4j");
        
        builder.addText("ZipCenOp")
                .summary("442048209as")
                .url("https://github.com/442048209as/ZipCenOp");
    }

}