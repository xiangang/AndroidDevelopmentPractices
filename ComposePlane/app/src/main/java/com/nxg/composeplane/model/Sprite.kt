package com.nxg.composeplane.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nxg.composeplane.R
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * 精灵状态枚举类
 */
enum class SpriteState {
    LIFE, // 存活
    DEATH, //死亡
}

/**
 * 精灵基类
 */
@InternalCoroutinesApi
open class Sprite(
    open var id: Long = System.currentTimeMillis(), //id
    open var name: String = "精灵之父", //名称
    @DrawableRes open val drawableIds: List<Int> = listOf(
        R.drawable.sprite_player_plane_1,
        R.drawable.sprite_player_plane_2
    ),//资源图标
    @DrawableRes open val bombDrawableId: Int = R.drawable.sprite_explosion_seq, //敌机爆炸帧动画资源
    open var segment: Int = 14, //爆炸效果由segment个片段组成:玩家飞机是4，小飞机是3，中飞机是4大飞机是6，explosion是14
    open var x: Int = 0, //x周坐标起点
    open var y: Int = 0, //y轴坐标起点
    open var width: Dp = BULLET_SPRITE_WIDTH.dp, //宽
    open var height: Dp = BULLET_SPRITE_HEIGHT.dp, //高
    open var speed: Int = 500, //飞行速度
    open var state: SpriteState = SpriteState.LIFE, //飞出屏幕或发生碰撞代表死亡
) {

    fun isAlive() = state == SpriteState.LIFE

    fun isDead() = state == SpriteState.DEATH

    open fun reBirth() {
        state = SpriteState.LIFE
    }

    fun die() {
        state = SpriteState.DEATH
    }

    override fun toString(): String {
        return "Sprite(id=$id, name='$name', drawableIds=$drawableIds, bombDrawableId=$bombDrawableId, segment=$segment, x=$x, y=$y, width=$width, height=$height, speed=$speed, state=$state)"
    }


}


/**
 * 玩家飞机精灵
 */
const val PLAYER_PLANE_SPRITE_SIZE = 60
const val PLAYER_PLANE_SPRITE_POINT_X = -500
const val PLAYER_PLANE_SPRITE_POINT_Y = -500
const val PLAYER_PLANE_PROTECT = 3

@InternalCoroutinesApi
data class PlayerPlane(
    override var id: Long = System.currentTimeMillis(), //id
    override var name: String = "雷电",
    @DrawableRes override val drawableIds: List<Int> = listOf(
        R.drawable.sprite_player_plane_1,
        R.drawable.sprite_player_plane_2
    ), //玩家飞机资源图标
    @DrawableRes val bombDrawableIds: Int = R.drawable.sprite_player_plane_bomb_seq, //玩家飞机爆炸帧动画资源
    override var segment: Int = 4, //爆炸效果由segment个片段组成
    override var x: Int = PLAYER_PLANE_SPRITE_POINT_X, //玩家飞机在X轴上的位置
    override var y: Int = PLAYER_PLANE_SPRITE_POINT_Y, //玩家飞机在Y轴上的位置
    override var width: Dp = PLAYER_PLANE_SPRITE_SIZE.dp, //宽
    override var height: Dp = PLAYER_PLANE_SPRITE_SIZE.dp, //高
    override var speed: Int = 200, //飞行速度，沿着Y轴从屏幕底部往屏幕顶部（屏幕高度）飞行一次所花费的时间
    var protect: Int = 3, //刚出现时的闪烁次数（此时无敌状态）
    var animateIn: Boolean = true, //飞入动画标志位
) : Sprite() {

    /**
     * 减少保护次数，为0的时候碰撞即爆炸
     */
    fun reduceProtect() {
        if (protect > 0) {
            protect--
        }
    }

    fun isNoProtect() = protect <= 0

    override fun reBirth() {
        animateIn = true
        state = SpriteState.LIFE
        protect = PLAYER_PLANE_PROTECT
    }
}

/**
 * 子弹精灵
 */
const val BULLET_SPRITE_WIDTH = 6
const val BULLET_SPRITE_HEIGHT = 18

@InternalCoroutinesApi
data class Bullet(
    override var id: Long = System.currentTimeMillis(), //id
    override var name: String = "蓝色单发子弹",
    @DrawableRes val drawableId: Int = R.drawable.sprite_bullut_single, //子弹资源图标
    override var width: Dp = BULLET_SPRITE_WIDTH.dp, //宽
    override var height: Dp = BULLET_SPRITE_HEIGHT.dp, //高
    override var speed: Int = 200, //飞行速度，从玩家飞机头部沿着Y轴往屏幕顶部飞行一次屏幕高度所花费的时间
    var offsetX: Int = -100, //子弹在X轴上与玩家飞机的偏移量
    var offsetY: Int = -100, //子弹在Y轴上与玩家飞机的偏移量,
    var hit: Int = 1, //击打能力，击中一次敌人，敌人减掉的生命值
) : Sprite()

/**
 * 敌机精灵
 */
const val SMALL_ENEMY_PLANE_SPRITE_SIZE = 40
const val MIDDLE_ENEMY_PLANE_SPRITE_SIZE = 60
const val BIG_ENEMY_PLANE_SPRITE_SIZE = 100

@InternalCoroutinesApi
data class EnemyPlane(
    override var id: Long = System.currentTimeMillis(), //id
    override var name: String = "敌军侦察机",
    @DrawableRes override val drawableIds: List<Int> = listOf(R.drawable.sprite_small_enemy_plane), //飞机资源图标
    @DrawableRes override val bombDrawableId: Int = R.drawable.sprite_small_enemy_plane_seq, //敌机爆炸帧动画资源
    override var segment: Int = 3, //爆炸效果由segment个片段组成，小飞机是3，中飞机是4，大飞机是6
    override var x: Int = 0, //敌机当前在X轴上的位置
    override var y: Int = -100, //敌机当前在Y轴上的位置
    override var width: Dp = SMALL_ENEMY_PLANE_SPRITE_SIZE.dp, //宽
    override var height: Dp = SMALL_ENEMY_PLANE_SPRITE_SIZE.dp, //高
    override var speed: Int = 6000, //飞行速度，从屏幕顶端部沿着Y轴飞行到屏幕底部所花费的时间
    val power: Int = 1, //生命值，敌机的抗打击能力
    var hit: Int = 0, //被击中消耗的生命值
    val value: Int = 10, //打一个敌机的得分
    var startY: Int = -100 //出现的起始位置
) : Sprite() {

    fun beHit(reduce: Int) {
        hit += reduce
    }

    fun isNoPower() = (power - hit) <= 0

    override fun reBirth() {
        state = SpriteState.LIFE
        hit = 0
    }

    override fun toString(): String {
        return "EnemyPlane(state=$state, id=$id, name='$name', drawableIds=$drawableIds, bombDrawableId=$bombDrawableId, segment=$segment, x=$x, y=$y, width=$width, height=$height, speed=$speed, power=$power, hit=$hit, value=$value, startY=$startY)"
    }


}

/**
 * 爆炸动画精灵
 */
@InternalCoroutinesApi
data class Bomb(
    override var id: Long = System.currentTimeMillis(), //id
    override var name: String = "爆炸动画",
    @DrawableRes override var bombDrawableId: Int = R.drawable.sprite_explosion_seq, //爆炸帧动画资源
    override var segment: Int = 14, //爆炸效果由segment个片段组成，小飞机是3，中飞机是4，大飞机是6，explosion是14
    override var x: Int = 200, //当前在X轴上的位置
    override var y: Int = 200, //当前在Y轴上的位置
    override var width: Dp = BIG_ENEMY_PLANE_SPRITE_SIZE.dp, //宽
    override var height: Dp = BIG_ENEMY_PLANE_SPRITE_SIZE.dp, //高
    override var state: SpriteState = SpriteState.DEATH //爆炸动画默认死亡
) : Sprite()