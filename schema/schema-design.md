# 剧本 YAML Schema 设计说明

## 1. 设计目标

将一部小说稳定、可机读地拆解为「**剧本 → 人物 → 场景 → 节拍**」四层结构，使 AI 输出既能被程序直接消费（生成 Markdown / Final Draft / Word），也能被作者直接打开编辑。

## 2. 四层结构

| 层级 | 字段 | 作用 |
| ---- | ---- | ---- |
| 剧本 | `title / original_work / summary / metadata` | 整体信息与统计 |
| 人物 | `characters[]` | 全剧人物表，统一别名 |
| 场景 | `scenes[]` | 时间 + 地点 + 在场人物 + 环境 |
| 节拍 | `scenes[].beats[]` | 场景内最小情节单元 |

节拍类型：

- `action`：纯动作描写
- `dialogue`：人物对白
- `narration`：旁白 / 叙述
- `transition`：转场（淡入、切至、闪回 …）

## 3. 设计权衡

1. **分层结构清晰**
   四层结构对齐主流剧本（Final Draft、Fountain）的概念，作者无需额外学习成本。
2. **信息完整可追溯**
   `original_work` 保留原著元数据；人物 `alias` 字段处理「李白 / 太白 / 青莲居士」这类小说常见的称呼漂移问题。
3. **机器友好**
   每个实体都有稳定 ID（`char_001`、`scene_007`、`beat_007_03`），便于跨表引用、版本 diff 与下游导出。
4. **人类友好**
   YAML 缩进直观；可选字段（`emotion / action / transitions`）只在必要时出现，不污染视觉。
5. **可扩展**
   `metadata.tags` 与节拍可选字段为后续「情绪曲线分析」「拍摄分镜辅助」等迭代留足空间。

## 4. ID 命名约定

| 实体 | 形式 | 示例 |
| ---- | ---- | ---- |
| 人物 | `char_{3 位序号}` | `char_001` |
| 场景 | `scene_{3 位序号}` | `scene_007` |
| 节拍 | `beat_{场景序号}_{2 位场内序号}` | `beat_007_03` |

序号统一从 `001` 开始递增，跨章节连续编号。

## 5. 校验规则

- `characters[].importance` 必须为 `main / supporting / minor` 之一
- `scenes[].beats[].type` 必须为 `action / dialogue / narration / transition` 之一
- `dialogue` 与 `action` 类型节拍 `character` 字段必填，且必须命中 `characters[].id`
- `scenes[].characters[]` 中所有 ID 必须存在于 `characters[]`
- `metadata.total_scenes == scenes.length`
- `metadata.total_characters == characters.length`

后端在 YAML 生成阶段执行上述校验，前端编辑器在保存时再次校验。

## 6. 版本

当前 Schema 版本：**v1.0**。后续不兼容变更将提升主版本号，并在 `original_work.version` 中标注实际产出版本。
