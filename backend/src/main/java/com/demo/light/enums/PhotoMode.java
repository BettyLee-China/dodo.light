package com.demo.light.enums;
/**
 * 照片拍摄模式枚举
 * 用于表示相机或手机应用中的不同拍摄场景模式。
 */
public enum PhotoMode {

    // 枚举常量及其参数
    LANDSCAPE("风景", "优化广角和色彩，适合拍摄山脉、建筑等广阔场景。"),
    PORTRAIT("人像", "使用大光圈效果虚化背景，突出人物主体。"),
    NIGHT("夜景", "延长曝光时间，提高感光度，捕捉暗光环境下的细节。"),
    MACRO("微距", "对焦于非常近的物体，用于拍摄花卉、昆虫等细节。"),
    SPORTS("运动", "使用高速快门捕捉快速移动的物体，减少运动模糊。"),
    FOOD("美食", "增强食物的色彩饱和度，使其看起来更诱人。");
//    AUTO("自动", "相机自动分析场景并选择最佳设置。");

    // 枚举实例的属性
    private final String displayName; // 显示名称（中文）
    private final String description; // 描述信息

    /**
     * 私有构造函数，用于初始化枚举常量的属性。
     * @param displayName 显示名称
     * @param description 描述
     */
    PhotoMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    // Getter 方法
    /**
     * 获取该模式的显示名称。
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取该模式的详细描述。
     * @return 描述信息
     */
    public String getDescription() {
        return description;
    }

    /**
     * 重写 toString 方法，返回显示名称。
     * @return 显示名称
     */
    @Override
    public String toString() {
        return displayName;
    }


}
