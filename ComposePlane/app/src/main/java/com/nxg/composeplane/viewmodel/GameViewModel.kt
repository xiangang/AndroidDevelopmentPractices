package com.nxg.composeplane.viewmodel

import android.app.Application
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nxg.composeplane.R
import com.nxg.composeplane.model.*
import com.nxg.composeplane.util.DensityUtil
import com.nxg.composeplane.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong

@InternalCoroutinesApi
class GameViewModel(application: Application) : AndroidViewModel(application) {

    companion object {

    }

    //id
    val id = AtomicLong(0L)

    /**
     * 游戏状态StateFlow
     */
    private val _gameStateFlow = MutableStateFlow(GameState.Waiting)

    val gameStateFlow = _gameStateFlow.asStateFlow()

    fun onGameStateFlowChange(newGameSate: GameState) {
        _gameStateFlow.value = newGameSate
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _gameStateFlow.emit(newGameSate)
            }
        }
    }

    /**
     * 玩家飞机StateFlow
     */
    private val _playerPlaneFlow = MutableStateFlow(PlayerPlane())

    val playerPlaneFlow = _playerPlaneFlow.asStateFlow()

    private fun onPlayerPlaneFlowChange(plane: PlayerPlane) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _playerPlaneFlow.emit(plane)
            }
        }
    }

    /**
     * 玩家飞机移动
     */
    fun onPlayerPlaneMove(x: Int, y: Int) {
        val playerPlane = playerPlaneFlow.value
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                playerPlane.x = x
                playerPlane.y = y
                _playerPlaneFlow.emit(playerPlane)
            }
        }
    }

    /**
     * 玩家飞机重生
     */
    private fun onPlayerPlaneReBirth() {
        val playerPlane = playerPlaneFlow.value
        playerPlane.reBirth()
        onPlayerPlaneFlowChange(playerPlane)
    }

    /**
     * 敌机StateFlow
     */
    private val _enemyPlaneListFlow = MutableStateFlow(mutableListOf<EnemyPlane>())

    val enemyPlaneListFlow = _enemyPlaneListFlow.asStateFlow()

    private fun onEnemyPlaneListFlowChange(list: List<EnemyPlane>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _enemyPlaneListFlow.emit(list as MutableList<EnemyPlane>)
            }
        }
    }

    /**
     * 子弹StateFlow
     */
    private val _bulletListFlow = MutableStateFlow(mutableListOf<Bullet>())

    val bulletListFlow = _bulletListFlow.asStateFlow()

    private fun onBulletListFlowChange(list: List<Bullet>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _bulletListFlow.emit(list as MutableList<Bullet>)
            }
        }
    }

    /**
     * 爆炸动画StateFlow
     */
    private val _bombListFlow = MutableStateFlow(listOf<Bomb>())

    val bombListFlow = _bombListFlow.asStateFlow()

    private fun onBombListFlowChange(list: List<Bomb>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _bombListFlow.emit(list)
            }
        }
    }

    /**
     * 分数记录
     */
    private val _gameScore = MutableLiveData(0)
    val gameScore: LiveData<Int> = _gameScore

    fun onGameScoreChange(score: Int) {
        _gameScore.value = score
    }

    /**
     * 难度等级
     */
    private val _gameLevel = MutableLiveData(0)
    val gameLevel: LiveData<Int> = _gameLevel

    fun onGameLevelChange(level: Int) {
        if (_gameLevel.value != level) {
            _gameLevel.value = level
            when (level) {
                1 -> createEnemySprite(3, 2, 1)
                2 -> createEnemySprite(6, 3, 2)
                3 -> createEnemySprite(10, 5, 3)
            }
        }
    }

    init {

        viewModelScope.launch {
            gameStateFlow.collect {
                LogUtil.printLog(message = "viewModelScope gameState $it")

                if (it == GameState.Running) {
                    running()
                }
            }
            bombListFlow.collect {
                LogUtil.printLog(message = "viewModelScope bombList $it")
                for (bomb in it) {
                    LogUtil.printLog(message = "viewModelScope bomb ${bomb}")
                }
            }
        }

        createPlayerSprite()

        onGameLevelChange(1)
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
        val playerPlane = playerPlaneFlow.value
        playerPlane.startX = startX
        playerPlane.startY = startY
        playerPlane.reBirth()
        onPlayerPlaneFlowChange(playerPlane)
    }

    /**
     * 生成子弹
     */
    fun createBulletSprite() {
        //游戏开始并且飞机在屏幕内才会生成
        if (gameStateFlow.value == GameState.Running && playerPlaneFlow.value.y < getApplication<Application>().resources.displayMetrics.heightPixels) {
            val bulletList = bulletListFlow.value as ArrayList
            val firstBullet = bulletList.firstOrNull { it.isDead() }
            if (firstBullet == null) {
                bulletList.add(Bullet(state = SpriteState.LIFE, init = false))
            } else {
                val newBullet = firstBullet.copy(state = SpriteState.LIFE, init = false)
                bulletList.add(newBullet)
                bulletList.removeAt(0)
            }
            onBulletListFlowChange(bulletList)
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
            speed = 6000,
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
            speed = 8000,
            velocity = 8,
            segment = 4,
            power = 4,
            value = 30
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
            speed = 12000,
            velocity = 4,
            segment = 6,
            power = 9,
            value = 80
        )
        val listEnemyPlane = enemyPlaneListFlow.value
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
        onEnemyPlaneListFlowChange(listEnemyPlane)
    }

    private fun running() {

        //分数归零
        onGameScoreChange(0)

        //玩家飞机重生
        onPlayerPlaneReBirth()

    }
}