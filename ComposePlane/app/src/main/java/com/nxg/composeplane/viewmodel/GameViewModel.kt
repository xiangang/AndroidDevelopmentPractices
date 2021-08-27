package com.nxg.composeplane.viewmodel

import android.app.Application
import android.graphics.Point
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
     * 敌机StateFlow
     */
    private val _enemyPlaneListFlow = MutableStateFlow(listOf<EnemyPlane>())

    val enemyPlaneListFlow = _enemyPlaneListFlow.asStateFlow()

    private fun onEnemyPlaneListFlowChange(list: List<EnemyPlane>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _enemyPlaneListFlow.emit(list)
            }
        }
    }

    /**
     * 子弹StateFlow
     */
    private val _bulletListFlow = MutableStateFlow(listOf<Bullet>())

    val bulletListFlow = _bulletListFlow.asStateFlow()

    private fun onBulletListFlowChange(list: List<Bullet>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _bulletListFlow.emit(list)
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

        val playerPlane = playerPlaneFlow.value
        playerPlane.reBirth()
        onPlayerPlaneFlowChange(playerPlane)

        //初始化玩家飞机出生位置
        val playerPlaneSizePx = DensityUtil.dp2px(resources, PLAYER_PLANE_SPRITE_SIZE)
        onPlayerPlaneMove(widthPixels / 2 - playerPlaneSizePx!! / 2, (heightPixels * 1.5).toInt())

        //初始化子弹(10连发)
        onBulletListFlowChange(
            listOf(
                Bullet(),
                Bullet(),
                Bullet(),
                Bullet(),
                Bullet(),
                Bullet(),
                Bullet(),
                Bullet(),
                Bullet(),
                Bullet(),
            )
        )

        createEnemySprite(heightPixels)
    }

    /**
     * 生成敌机
     */
    private fun createEnemySprite(heightPixels: Int) {

        val id = AtomicLong(0L)

        //初始化敌机
        val smallEnemyPlane = EnemyPlane(
            id = id.getAndIncrement(),
            name = "敌军侦察机",
            drawableIds = listOf(R.drawable.sprite_small_enemy_plane),
            bombDrawableId = R.drawable.sprite_small_enemy_plane_seq,
            startY = (-heightPixels..0).random(),
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
            segment = 6,
            power = 9,
            value = 80
        )
        val listEnemyPlane = mutableListOf<EnemyPlane>()
        //敌机飞行距离给定是高度的两倍，随机生成的时候要控制在这个范围内
        listEnemyPlane.add(smallEnemyPlane)
        listEnemyPlane.add(
            smallEnemyPlane.copy(
                id = id.getAndIncrement(),
                startY = (-heightPixels..0).random()
            )
        )
        listEnemyPlane.add(
            smallEnemyPlane.copy(
                id = id.getAndIncrement(),
                startY = (-heightPixels..0).random()
            )
        )
        listEnemyPlane.add(middleEnemyPlane)
        listEnemyPlane.add(
            middleEnemyPlane.copy(
                id = id.getAndIncrement(),
                startY = (-heightPixels * 1.5.toInt()..-heightPixels).random()
            )
        )
        listEnemyPlane.add(
            bigEnemyPlane.copy(
                id = id.getAndIncrement(),
                startY = (-heightPixels * 2..-heightPixels * 1.5.toInt()).random()
            )
        )
        onEnemyPlaneListFlowChange(listEnemyPlane)

        //初始化和敌机数量一致的爆炸精灵
        val bombList = mutableListOf<Bomb>()
        for (enemyPlane in listEnemyPlane) {
            bombList.add(Bomb(id = enemyPlane.id))
        }
        onBombListFlowChange(bombList)

    }

    private fun running() {

        onGameScoreChange(0)

    }
}