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
    open var type: Int = 0, //类型
    @DrawableRes open val drawableIds: List<Int> = listOf(
        R.drawable.sprite_player_plane_1,
        R.drawable.sprite_player_plane_2
    ),//资源图标
    @DrawableRes open val bombDrawableId: Int = R.drawable.sprite_explosion_seq, //敌机爆炸帧动画资源
    open var segment: Int = 14, //爆炸效果由segment个片段组成:玩家飞机是4，小飞机是3，中飞机是4大飞机是6，explosion是14
    open var x: Int = 0, //实时x轴坐标
    open var y: Int = 0, //实时y轴坐标
    open var startX: Int = -100, //出现的起始位置
    open var startY: Int = -100, //出现的起始位置
    open var width: Dp = BULLET_SPRITE_WIDTH.dp, //宽
    open var height: Dp = BULLET_SPRITE_HEIGHT.dp, //高
    open var velocity: Int = 40, //飞行速度（每帧移动的像素）
    open var state: SpriteState = SpriteState.LIFE, //控制是否显示
    open var init: Boolean = false, //是否初始化，主要用于精灵金初始化起点x，y坐标等，这里为什么不用state控制？state用于否显示，init用于重新初始化数据，而且必须是精灵离开屏幕后（走完整个移动的周期）才能重新初始化
) {

    fun isAlive() = state == SpriteState.LIFE

    fun isDead() = state == SpriteState.DEATH

    open fun reBirth() {
        state = SpriteState.LIFE
    }

    open fun die() {
        state = SpriteState.DEATH
    }

    override fun toString(): String {
        return "Sprite(id=$id, name='$name', drawableIds=$drawableIds, bombDrawableId=$bombDrawableId, segment=$segment, x=$x, y=$y, width=$width, height=$height, state=$state)"
    }
}


/**
 * 玩家飞机精灵
 */
const val PLAYER_PLANE_SPRITE_SIZE = 60
const val PLAYER_PLANE_PROTECT = 60

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
    override var x: Int = -100, //玩家飞机在X轴上的位置
    override var y: Int = -100, //玩家飞机在Y轴上的位置
    override var width: Dp = PLAYER_PLANE_SPRITE_SIZE.dp, //宽
    override var height: Dp = PLAYER_PLANE_SPRITE_SIZE.dp, //高
    var protect: Int = PLAYER_PLANE_PROTECT, //刚出现时的闪烁次数（此时无敌状态）
    var life: Int = 1, //生命(几条命的意思，不像敌机，可以经受多次击打，玩家飞机碰一下就Over)
    var animateIn: Boolean = true, //是否需要出场动画
    var bulletAward: Int = BULLET_DOUBLE shl 16 or 0, //子弹奖励（子弹类型 | 子弹数量），类型0是单发红色子弹，1是蓝色双发子弹
    var bombAward: Int = 0 shl 16 or 0, //爆炸奖励（爆炸类型 | 爆炸数量），目前类型只有0
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
        state = SpriteState.LIFE
        animateIn = true
        x = startX
        y = startY
        protect = PLAYER_PLANE_PROTECT
        bulletAward = 0
        bombAward = 0
    }
}

/**
 * 子弹精灵
 */
const val BULLET_SPRITE_WIDTH = 6
const val BULLET_SPRITE_HEIGHT = 18
const val BULLET_SINGLE = 0
const val BULLET_DOUBLE = 1

@InternalCoroutinesApi
data class Bullet(
    override var id: Long = System.currentTimeMillis(), //id
    override var name: String = "蓝色单发子弹",
    override var type: Int = BULLET_SINGLE, //类型:0单发子弹，1双发子弹
    @DrawableRes val drawableId: Int = R.drawable.sprite_bullet_single, //子弹资源图标
    override var width: Dp = BULLET_SPRITE_WIDTH.dp, //宽
    override var height: Dp = BULLET_SPRITE_HEIGHT.dp, //高
    override var x: Int = 0, //实时x轴坐标
    override var y: Int = 0, //实时y轴坐标
    override var state: SpriteState = SpriteState.DEATH, //默认死亡
    override var init: Boolean = false, //默认未初始化
    var hit: Int = 1,//击打能力，击中一次敌人，敌人减掉的生命值
) : Sprite() {

    /**
     * 是否失效，飞出屏幕就失效(这里判断y<0就行了，如果y<0时跟敌机碰撞，敌机肯定是在屏幕外看不到的，所以这里这么处理是可以的)
     */
    fun isInvalid() = this.y < 0

    /**
     * 射击(位移)
     */
    fun shoot() {
        this.x = this.startX
        this.y -= this.velocity
    }
}


/**
 * 道具奖励精灵
 */

const val AWARD_BULLET = 0
const val AWARD_BOMB = 1

@InternalCoroutinesApi
data class Award(
    override var id: Long = System.currentTimeMillis(), //id
    override var name: String = "子弹道具奖励",
    override var type: Int = AWARD_BULLET, //类型:0子弹道具奖励，1爆炸道具奖励
    @DrawableRes var drawableId: Int = R.drawable.sprite_blue_shot_down, //子弹资源图标
    override var width: Dp = 50.dp, //宽
    override var height: Dp = 80.dp, //高
    override var velocity: Int = 20, //飞行速度（每帧移动的像素）
    override var x: Int = 0, //实时x轴坐标
    override var y: Int = 0, //实时y轴坐标
    override var state: SpriteState = SpriteState.DEATH, //默认死亡
    override var init: Boolean = false, //默认未初始化
    var amount: Int = 1, //数量

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
    override var startY: Int = -100, //出现的起始位置
    override var width: Dp = SMALL_ENEMY_PLANE_SPRITE_SIZE.dp, //宽
    override var height: Dp = SMALL_ENEMY_PLANE_SPRITE_SIZE.dp, //高
    override var velocity: Int = 1, //飞行速度（每帧移动的像素）
    var bombX: Int = -100, //爆炸动画当前在X轴上的位置
    var bombY: Int = -100, //爆炸动画当前在Y轴上的位置
    val power: Int = 1, //生命值，敌机的抗打击能力
    var hit: Int = 0, //被击中消耗的生命值
    val value: Int = 10, //打一个敌机的得分

) : Sprite() {

    fun beHit(reduce: Int) {
        hit += reduce
    }

    fun isNoPower() = (power - hit) <= 0

    fun bomb() {
        hit = power
    }

    override fun reBirth() {
        state = SpriteState.LIFE
        hit = 0
    }

    override fun die() {
        state = SpriteState.DEATH
        bombX = x
        bombY = y
    }

    override fun toString(): String {
        return "EnemyPlane(state=$state, id=$id, name='$name', drawableIds=$drawableIds, bombDrawableId=$bombDrawableId, segment=$segment, x=$x, y=$y, width=$width, height=$height, power=$power, hit=$hit, value=$value, startY=$startY)"
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
    override var state: SpriteState = SpriteState.DEATH //爆炸动画默认不显示
) : Sprite()