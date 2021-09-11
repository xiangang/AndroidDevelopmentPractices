package com.nxg.composeplane.viewmodel

import android.app.Application
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.composeplane.R
import com.nxg.composeplane.model.*
import com.nxg.composeplane.util.DensityUtil
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.SoundPoolUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToInt

@InternalCoroutinesApi
class GameViewModel(application: Application) : AndroidViewModel(application) {

    //id
    val id = AtomicLong(0L)

    /**
     * 游戏状态StateFlow
     */
    private val _gameStateFlow = MutableStateFlow(GameState.Waiting)

    val gameStateFlow = _gameStateFlow.asStateFlow()

    private fun onGameStateFlowChange(newGameSate: GameState) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _gameStateFlow.emit(newGameSate)
            }
        }
    }

    /**
     * 玩家飞机StateFlow
     */
    private val _playerPlaneStateFlow = MutableStateFlow(PlayerPlane())

    val playerPlaneStateFlow = _playerPlaneStateFlow.asStateFlow()

    private fun onPlayerPlaneStateFlowChange(plane: PlayerPlane) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _playerPlaneStateFlow.emit(plane)
            }
        }
    }

    /**
     * 玩家飞机移动
     */
    private fun onPlayerPlaneMove(x: Int, y: Int) {
        if (gameStateFlow.value != GameState.Running) {
            return
        }
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.x = x
        playerPlane.y = y
        if (playerPlane.animateIn) {
            playerPlane.animateIn = false
        }
        onPlayerPlaneStateFlowChange(playerPlane)
    }

    /**
     * 玩家飞机奖励子弹
     */
    private fun onPlayerAwardBullet(bulletAward: Int) {
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.bulletAward = bulletAward
        onPlayerPlaneStateFlowChange(playerPlane)
    }

    /**
     * 玩家飞机奖励爆炸道具
     */
    private fun onPlayerAwardBomb(bombAward: Int) {
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.bombAward = bombAward
        onPlayerPlaneStateFlowChange(playerPlane)
    }

    /**
     * 玩家飞机重生
     */
    private fun onPlayerPlaneReBirth() {
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.reBirth()
        onPlayerPlaneStateFlowChange(playerPlane)
    }

    /**
     * 敌机StateFlow
     */
    private val _enemyPlaneListStateFlow = MutableStateFlow(mutableListOf<EnemyPlane>())

    val enemyPlaneListStateFlow = _enemyPlaneListStateFlow.asStateFlow()

    private fun onEnemyPlaneListStateFlowChange(list: MutableList<EnemyPlane>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _enemyPlaneListStateFlow.emit(list)
            }
        }
    }

    /**
     * 屏幕内所有敌机爆炸
     */
    private fun onDestroyAllEnemy() {
        viewModelScope.launch {
            //敌机全部消失
            val listEnemyPlane = enemyPlaneListStateFlow.value
            var countScore = 0
            withContext(Dispatchers.Default) {
                for (enemyPlane in listEnemyPlane) {
                    //存活并且在屏幕内
                    if (enemyPlane.isAlive() && !enemyPlane.isNoPower() && enemyPlane.y > 0 && enemyPlane.y < getApplication<Application>().resources.displayMetrics.heightPixels) {
                        countScore += enemyPlane.value
                        enemyPlane.bomb()//能量归零就爆炸
                    }
                }
                _enemyPlaneListStateFlow.emit(listEnemyPlane)
            }
            //更新分数
            gameScoreStateFlow.value.plus(countScore).let { onGameScoreStateFlowChange(it) }

            //爆炸道具减1
            val bombAward = playerPlaneStateFlow.value.bombAward
            var bombNum = bombAward and 0xFFFF //数量
            val bombType = bombAward shr 16 //类型
            if (bombNum-- <= 0) {
                bombNum = 0
            }
            onPlayerAwardBomb(bombType shl 16 or bombNum)
        }
    }

    /**
     * 子弹StateFlow
     */
    private val _bulletListStateFlow = MutableStateFlow(mutableListOf<Bullet>())

    val bulletListStateFlow = _bulletListStateFlow.asStateFlow()

    private fun onBulletListStateFlowChange(list: List<Bullet>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _bulletListStateFlow.emit(list as MutableList<Bullet>)
            }
        }
    }

    /**
     * 道具奖励tateFlow
     */
    private val _awardListStateFlow = MutableStateFlow(CopyOnWriteArrayList<Award>())

    val awardListStateFlow = _awardListStateFlow.asStateFlow()

    private fun onAwardListStateFlowChange(list: CopyOnWriteArrayList<Award>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _awardListStateFlow.emit(list)
            }
        }
    }

    private fun onAwardRemove(award: Award) {
        val awardList = awardListStateFlow.value
        awardList.remove(award)
        onAwardListStateFlowChange(awardList)
    }

    /**
     * 分数记录
     */
    private val _gameScoreStateFlow = MutableStateFlow(0)
    val gameScoreStateFlow = _gameScoreStateFlow.asStateFlow()

    private fun onGameScoreStateFlowChange(score: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _gameScoreStateFlow.emit(score)
            }
        }
    }

    /**
     * 难度等级
     */
    private val _gameLevelStateFlow = MutableStateFlow(0)

    private fun onGameLevelStateFlowChange(level: Int) {
        if (_gameLevelStateFlow.value != level) {
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    _gameLevelStateFlow.emit(level)
                }
            }
            when (level) {
                1 -> createEnemySprite(3, 2, 1)
                2 -> createEnemySprite(6, 3, 2)
                3 -> createEnemySprite(10, 5, 3)
            }
        }
    }


    init {

        viewModelScope.launch {
            //初始化SoundPoolUtil
            withContext(Dispatchers.Default) {
                SoundPoolUtil.getInstance(application.applicationContext)
            }

            gameStateFlow.collect {
                LogUtil.printLog(message = "viewModelScope gameState $it")

            }
        }

        createPlayerSprite()

        onGameLevelStateFlowChange(1)
    }

    /**
     * 生成玩家飞机
     */
    private fun createPlayerSprite() {
        //获取屏幕宽高
        val resources = getApplication<Application>().resources
        val dm = resources.displayMetrics
        val widthPixels = dm.widthPixels
        val heightPixels = dm.heightPixels

        //初始化玩家飞机出生位置
        val playerPlaneSizePx = DensityUtil.dp2px(resources, PLAYER_PLANE_SPRITE_SIZE)
        val startX = widthPixels / 2 - playerPlaneSizePx!! / 2
        val startY = (heightPixels * 1.5).toInt()
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.startX = startX
        playerPlane.startY = startY
        playerPlane.reBirth()
        LogUtil.printLog(message = "createPlayerSprite playerPlane $playerPlane")
        onPlayerPlaneStateFlowChange(playerPlane)
    }

    /**
     * 生成子弹
     */
    private fun createBullet() {
        //游戏开始并且飞机在屏幕内才会生成
        if (gameStateFlow.value == GameState.Running && playerPlaneStateFlow.value.y < getApplication<Application>().resources.displayMetrics.heightPixels) {
            val bulletAward = playerPlaneStateFlow.value.bulletAward
            var bulletNum = bulletAward and 0xFFFF //数量
            val bulletType = bulletAward shr 16 //类型
            val bulletList = bulletListStateFlow.value as ArrayList
            LogUtil.printLog(message = "createBullet bulletNum $bulletNum bulletType $bulletType bulletList size ${bulletList.size}")
            val firstBullet = bulletList.firstOrNull { it.isDead() }
            if (firstBullet == null) {
                var newBullet = Bullet(
                    type = BULLET_SINGLE,
                    drawableId = R.drawable.sprite_bullet_single,
                    width = BULLET_SPRITE_WIDTH.dp,
                    hit = 1,
                    state = SpriteState.LIFE,
                    init = false
                )
                //子弹奖励
                if (bulletNum > 0 && bulletType == BULLET_DOUBLE) {
                    newBullet = newBullet.copy(
                        type = BULLET_DOUBLE,
                        drawableId = R.drawable.sprite_bullet_double,
                        width = 18.dp,
                        hit = 2,
                        state = SpriteState.LIFE,
                        init = false
                    )
                    //消耗子弹
                    bulletNum--
                    LogUtil.printLog(message = "createBullet bulletNum $bulletNum")
                    onPlayerAwardBullet(BULLET_DOUBLE shl 16 or bulletNum)
                }
                bulletList.add(newBullet)
            } else {
                var newBullet = firstBullet.copy(
                    type = BULLET_SINGLE,
                    drawableId = R.drawable.sprite_bullet_single,
                    width = BULLET_SPRITE_WIDTH.dp,
                    hit = 1,
                    state = SpriteState.LIFE,
                    init = false
                )
                //子弹奖励
                if (bulletNum > 0 && bulletType == BULLET_DOUBLE) {
                    newBullet = firstBullet.copy(
                        type = BULLET_DOUBLE,
                        drawableId = R.drawable.sprite_bullet_double,
                        width = 18.dp,
                        hit = 2,
                        state = SpriteState.LIFE,
                        init = false
                    )
                    //消耗子弹
                    bulletNum--
                    LogUtil.printLog(message = "createBullet bulletNum $bulletNum")
                    onPlayerAwardBullet(BULLET_DOUBLE shl 16 or bulletNum)
                }
                bulletList.add(newBullet)
                bulletList.removeAt(0)
            }
            onBulletListStateFlowChange(bulletList)
        }
    }

    /**
     * 初始化子弹出生位置
     */
    private fun initBullet(bullet: Bullet) {
        val playerPlane = playerPlaneStateFlow.value
        val playerPlaneWidthPx = dp2px(playerPlane.width)
        val bulletWidthPx = dp2px(bullet.width)
        val bulletHeightPx = dp2px(bullet.height)
        val startX = (playerPlane.x + playerPlaneWidthPx!! / 2 - bulletWidthPx!! / 2)
        val startY = (playerPlane.y - bulletHeightPx!!)
        bullet.startX = startX
        bullet.startY = startY
        bullet.x = bullet.startX
        bullet.y = bullet.startY
        bullet.init = true
    }


    /**
     * 生成道具奖励
     */
    private fun createAwardSprite() {
        LogUtil.printLog(message = "createAwardSprite() ---> ")
        //游戏开始才会生成
        if (gameStateFlow.value == GameState.Running) {
            val listAward = awardListStateFlow.value
            val type = (0..1).random()
            val award = Award(type = type)
            award.state = SpriteState.LIFE
            if (type == AWARD_BULLET) {
                award.drawableId = R.drawable.sprite_blue_shot_down
                award.amount = 100
            }
            if (type == AWARD_BOMB) {
                award.drawableId = R.drawable.sprite_red_bomb_down
                award.amount = 1

            }
            listAward.add(award)
            onAwardListStateFlowChange(listAward)
        }
    }

    /**
     * 生成敌机
     */
    private fun createEnemySprite(
        smallEnemyPlaneNum: Int,
        middleEnemyPlaneNum: Int,
        bigEnemyPlaneNum: Int
    ) {
        //获取屏幕宽高
        val heightPixels = getApplication<Application>().resources.displayMetrics.heightPixels

        //初始化敌机
        val smallEnemyPlane = EnemyPlane(
            id = id.getAndIncrement(),
            name = "敌军侦察机",
            drawableIds = listOf(R.drawable.sprite_small_enemy_plane),
            bombDrawableId = R.drawable.sprite_small_enemy_plane_seq,
            startY = (-heightPixels..0).random(),
            velocity = 10,
            segment = 3,
            power = 1,
            value = 10
        )
        val middleEnemyPlane = EnemyPlane(
            id = id.getAndIncrement(),
            name = "敌军战斗机",
            drawableIds = listOf(
                R.drawable.sprite_middle_enemy_plane_1,
                R.drawable.sprite_middle_enemy_plane_2
            ),
            bombDrawableId = R.drawable.sprite_middle_enemy_plane_bomb_seq,
            startY = (-heightPixels * 1.5.toInt()..-heightPixels).random(),
            width = MIDDLE_ENEMY_PLANE_SPRITE_SIZE.dp,
            height = MIDDLE_ENEMY_PLANE_SPRITE_SIZE.dp,
            velocity = 8,
            segment = 4,
            power = 4,
            value = 40
        )
        val bigEnemyPlane = EnemyPlane(
            id = id.getAndIncrement(),
            name = "敌军战舰",
            drawableIds = listOf(
                R.drawable.sprite_big_enemy_plane_1,
                R.drawable.sprite_big_enemy_plane_2,
                R.drawable.sprite_big_enemy_plane_3

            ),
            bombDrawableId = R.drawable.sprite_big_enemy_plane_bomb_seq,
            startY = (-heightPixels * 2..-heightPixels * 1.5.toInt()).random(),
            width = BIG_ENEMY_PLANE_SPRITE_SIZE.dp,
            height = BIG_ENEMY_PLANE_SPRITE_SIZE.dp,
            velocity = 4,
            segment = 6,
            power = 9,
            value = 90
        )
        val listEnemyPlane = enemyPlaneListStateFlow.value
        //敌机飞行距离给定是高度的两倍，随机生成的时候要控制在这个范围内
        listEnemyPlane.add(smallEnemyPlane)
        for (small in 1 until smallEnemyPlaneNum) {
            listEnemyPlane.add(
                smallEnemyPlane.copy(
                    id = id.getAndIncrement(),
                    startY = (-heightPixels..0).random()
                )
            )
        }

        listEnemyPlane.add(middleEnemyPlane)
        for (middle in 1 until middleEnemyPlaneNum) {
            listEnemyPlane.add(
                middleEnemyPlane.copy(
                    id = id.getAndIncrement(),
                    startY = (-heightPixels * 1.5.toInt()..-heightPixels).random()
                )
            )
        }

        for (big in 1 until bigEnemyPlaneNum) {
            listEnemyPlane.add(
                bigEnemyPlane.copy(
                    id = id.getAndIncrement(),
                    startY = (-heightPixels * 2..(-heightPixels * 1.5).toInt()).random()
                )
            )
        }
        for (enemyPlane in listEnemyPlane) {
            LogUtil.printLog(message = "createEnemySprite: enemyPlane $enemyPlane")
        }
        onEnemyPlaneListStateFlowChange(listEnemyPlane)
    }

    /**
     * 重置分数，玩家飞机重生
     */
    private fun resetGame() {

        //分数归零
        onGameScoreStateFlowChange(0)

        //玩家飞机重生
        onPlayerPlaneReBirth()

    }

    //游戏动作
    val onGameAction = GameAction(

        start = {
            onGameStateFlowChange(GameState.Running)
        },

        reset = {
            resetGame()
            onGameStateFlowChange(GameState.Waiting)
        },
        pause = {
            onGameStateFlowChange(GameState.Paused)

        },
        playerMove = { x, y ->
            run {
                onPlayerPlaneMove(x, y)
            }
        },
        score = { score ->
            run {
                //播放爆炸音效
                viewModelScope.launch {
                    withContext(Dispatchers.Default) {
                        SoundPoolUtil.getInstance(application.applicationContext)
                            .playByRes(R.raw.explosion)//播放res中的音频
                    }
                }

                //更新分数
                onGameScoreStateFlowChange(score)

                //简单处理，不同分数对应不同的等级
                if (score in 100..999) {
                    onGameLevelStateFlowChange(2)
                }
                if (score in 1000..1999) {
                    onGameLevelStateFlowChange(3)
                }

                //分数是100整数时，产生随机奖励
                if (score % 100 == 0) {
                    createAwardSprite()
                }
            }
        },
        award = { award ->
            run {
                //奖励子弹
                if (award.type == AWARD_BULLET) {
                    val bulletAward = playerPlaneStateFlow.value.bulletAward
                    var num = bulletAward and 0xFFFF //数量
                    num += award.amount
                    onPlayerAwardBullet(BULLET_DOUBLE shl 16 or num)
                }
                //奖励爆炸道具
                if (award.type == AWARD_BOMB) {
                    val bombAward = playerPlaneStateFlow.value.bombAward
                    var num = bombAward and 0xFFFF //数量
                    num += award.amount
                    onPlayerAwardBomb(0 shl 16 or num)
                }
                onAwardRemove(award)
            }
        },
        die = {
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    SoundPoolUtil.getInstance(application.applicationContext)
                        .playByRes(R.raw.explosion)//播放res中的音频
                }
            }

            onGameStateFlowChange(GameState.Dying)

        },
        over = {
            onGameStateFlowChange(GameState.Over)
        },
        exit = {
            onGameStateFlowChange(GameState.Exit)
        },
        destroyAllEnemy = {
            onDestroyAllEnemy()
        },
        shooting = { resId ->
            run {
                LogUtil.printLog(message = "onShooting resId $resId")
                viewModelScope.launch {
                    withContext(Dispatchers.Default) {
                        SoundPoolUtil.getInstance(application.applicationContext)
                            .playByRes(resId)//播放res中的音频
                    }
                }
            }
        },
        createBullet = { createBullet() },
        initBullet = { initBullet(it) },
    )
}

/**
 * dp转px
 */
@InternalCoroutinesApi
fun GameViewModel.dp2px(dp: Dp): Int? {
    val resources = getApplication<Application>().resources
    return DensityUtil.dp2px(resources, dp.value.roundToInt())
}