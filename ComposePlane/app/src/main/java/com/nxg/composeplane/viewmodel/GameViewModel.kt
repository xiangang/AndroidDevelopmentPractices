package com.nxg.composeplane.viewmodel

import android.app.Application
import android.content.Context
import androidx.annotation.RawRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.composeplane.R
import com.nxg.composeplane.model.*
import com.nxg.composeplane.util.DensityUtil
import com.nxg.composeplane.util.LogUtil
import com.nxg.composeplane.util.SoundPoolUtil
import com.nxg.composeplane.util.SpriteUtil
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
     * 子弹StateFlow
     */
    private val _bulletListStateFlow = MutableStateFlow(CopyOnWriteArrayList<Bullet>())

    val bulletListStateFlow = _bulletListStateFlow.asStateFlow()

    private fun onBulletListStateFlowChange(list: CopyOnWriteArrayList<Bullet>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _bulletListStateFlow.emit(list)
            }
        }
    }

    /**
     * 道具奖励StateFlow
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

    /**
     * 分数记录StateFlow
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
     * 难度等级StateFlow
     */
    private val _gameLevelStateFlow = MutableStateFlow(0)
    private val gameLevelStateFlow = _gameLevelStateFlow

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
        //初始化SoundPoolUtil
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                SoundPoolUtil.getInstance(application.applicationContext)
            }

            gameStateFlow.collect {
                LogUtil.printLog(message = "viewModelScope gameState $it")
            }

            gameLevelStateFlow.collect {
                LogUtil.printLog(message = "viewModelScope gameLevelStateFlow $it")

            }
        }

    }

    /**
     * 初始化
     */
    fun onGameInit() {

        initPlayerSprite()

        onGameLevelStateFlowChange(1)
    }

    /**
     * 玩家飞机初始化
     */
    private fun initPlayerSprite() {
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
        LogUtil.printLog(message = "initPlayerSprite playerPlane $playerPlane")
        onPlayerPlaneStateFlowChange(playerPlane)
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
     * 玩家飞机拖拽
     */
    private fun onDragPlayerPlane(dragAmount: Offset) {
        if (gameStateFlow.value != GameState.Running) {
            return
        }
        //获取屏幕宽高
        val resources = getApplication<Application>().resources
        val dm = resources.displayMetrics
        val widthPixels = dm.widthPixels
        val heightPixels = dm.heightPixels
        //获取玩家飞机
        val playerPlane = playerPlaneStateFlow.value
        val playerPlaneHeightPx = dp2px(playerPlane.height)
        var newOffsetX = playerPlane.x
        var newOffsetY = playerPlane.y
        //边界检测
        when {
            newOffsetX + dragAmount.x <= 0 -> {
                newOffsetX = 0
            }
            (newOffsetX + dragAmount.x + playerPlaneHeightPx!!) >= widthPixels -> {
                widthPixels.let {
                    newOffsetX = it - playerPlaneHeightPx
                }
            }
            else -> {
                newOffsetX += dragAmount.x.roundToInt()
            }
        }
        when {
            newOffsetY + dragAmount.y <= 0 -> {
                newOffsetY = 0
            }
            (newOffsetY + dragAmount.y) >= heightPixels -> {
                heightPixels.let {
                    newOffsetY = it
                }
            }
            else -> {
                newOffsetY += dragAmount.y.roundToInt()
            }
        }
        onPlayerPlaneMove(newOffsetX, newOffsetY)
    }

    /**
     * 玩家飞机获得子弹奖励
     */
    private fun onPlayerAwardBullet(bulletAward: Int) {
        val playerPlane = playerPlaneStateFlow.value
        playerPlane.bulletAward = bulletAward
        onPlayerPlaneStateFlowChange(playerPlane)
    }

    /**
     * 玩家飞机获得炸弹奖励
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
     * 子弹生成
     */
    private fun onCreateBullet() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                //游戏开始并且飞机在屏幕内才会生成
                if (gameStateFlow.value == GameState.Running && playerPlaneStateFlow.value.y < getApplication<Application>().resources.displayMetrics.heightPixels) {
                    val bulletAward = playerPlaneStateFlow.value.bulletAward
                    var bulletNum = bulletAward and 0xFFFF //数量
                    val bulletType = bulletAward shr 16 //类型
                    val bulletList = bulletListStateFlow.value
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
        }

    }

    /**
     * 初始化子弹出生位置
     */
    private fun onBulletMove(bullet: Bullet) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                //初始化起点(为什么单独搞一个init属性，因为init属性是添加到队里列时才设置false,这样渲染时检测init为false才去初始化起点.
                //如果根据isAlive来检测会导致Bullet一死亡就算重新初始化位置，但是复用重新发射时，飞机的位置可能已经变动了。
                if (!bullet.init) {

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

                    //播放射击音效，放到非UI线程
                    onPlayByRes(getApplication(), R.raw.shoot)
                }

                //子弹离开屏幕后则死亡
                if (bullet.isInvalid()) {
                    bullet.die()
                }

                //射击
                bullet.move()
            }
        }

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
     * 移除奖励
     */
    private fun onAwardRemove(award: Award) {
        val awardList = awardListStateFlow.value
        awardList.remove(award)
        onAwardListStateFlowChange(awardList)
    }


    /**
     * 生成敌机
     */
    private fun createEnemySprite(
        smallEnemyPlaneNum: Int,
        middleEnemyPlaneNum: Int,
        bigEnemyPlaneNum: Int
    ) {
        //初始化敌机
        val smallEnemyPlane = EnemyPlane(
            id = id.incrementAndGet(),
            name = "敌军侦察机",
            type = 0,
            drawableIds = listOf(R.drawable.sprite_small_enemy_plane),
            bombDrawableId = R.drawable.sprite_small_enemy_plane_seq,
            velocity = 10,
            segment = 3,
            power = 1,
            value = 10
        )
        val middleEnemyPlane = EnemyPlane(
            id = id.incrementAndGet(),
            name = "敌军战斗机",
            type = 1,
            drawableIds = listOf(
                R.drawable.sprite_middle_enemy_plane_1,
                R.drawable.sprite_middle_enemy_plane_2
            ),
            bombDrawableId = R.drawable.sprite_middle_enemy_plane_bomb_seq,
            width = MIDDLE_ENEMY_PLANE_SPRITE_SIZE.dp,
            height = MIDDLE_ENEMY_PLANE_SPRITE_SIZE.dp,
            velocity = 8,
            segment = 4,
            power = 4,
            value = 40
        )
        val bigEnemyPlane = EnemyPlane(
            id = id.incrementAndGet(),
            name = "敌军战舰",
            type = 2,
            drawableIds = listOf(
                R.drawable.sprite_big_enemy_plane_1,
                R.drawable.sprite_big_enemy_plane_2,
                R.drawable.sprite_big_enemy_plane_3

            ),
            bombDrawableId = R.drawable.sprite_big_enemy_plane_bomb_seq,
            width = BIG_ENEMY_PLANE_SPRITE_SIZE.dp,
            height = BIG_ENEMY_PLANE_SPRITE_SIZE.dp,
            velocity = 2,
            segment = 6,
            power = 18,
            value = 90
        )
        val listEnemyPlane = enemyPlaneListStateFlow.value
        //敌机飞行距离给定是高度的两倍，随机生成的时候要控制在这个范围内
        listEnemyPlane.add(smallEnemyPlane)
        for (small in 1 until smallEnemyPlaneNum) {
            val copy = smallEnemyPlane.copy()
            copy.id = id.incrementAndGet()
            listEnemyPlane.add(copy)
        }

        listEnemyPlane.add(middleEnemyPlane)
        for (middle in 1 until middleEnemyPlaneNum) {
            val copy = middleEnemyPlane.copy()
            copy.id = id.incrementAndGet()
            listEnemyPlane.add(copy)
        }

        for (big in 1 until bigEnemyPlaneNum) {
            val copy = bigEnemyPlane.copy()
            copy.id = id.incrementAndGet()
            listEnemyPlane.add(copy)
        }
        for (enemyPlane in listEnemyPlane) {
            LogUtil.printLog(message = "createEnemySprite: enemyPlane $enemyPlane")
        }
        onEnemyPlaneListStateFlowChange(listEnemyPlane)
    }

    /**
     * 敌机移动
     */
    private fun onEnemyPlaneMove(enemyPlane: EnemyPlane) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                //获取屏幕宽高
                val widthPixels = getApplication<Application>().resources.displayMetrics.widthPixels
                val heightPixels =
                    getApplication<Application>().resources.displayMetrics.heightPixels

                //敌机的大小和活动范围
                val enemyPlaneWidthPx = dp2px(enemyPlane.width)
                val enemyPlaneHeightPx = dp2px(enemyPlane.height)
                val maxEnemyPlaneSpriteX = widthPixels - enemyPlaneWidthPx!! //X轴屏幕宽度向左偏移一个机身
                val maxEnemyPlaneSpriteY = heightPixels * 1.5 //Y轴1.5倍屏幕高度

                //如果未初始化，则给个随机值(在屏幕范围内)
                if (!enemyPlane.init) {
                    enemyPlane.x = (0..maxEnemyPlaneSpriteX).random()
                    var newY = -(0..heightPixels).random() - (0..heightPixels).random()
                    when (enemyPlane.type) {
                        0 -> newY -= enemyPlaneHeightPx!! * 2
                        1 -> newY -= enemyPlaneHeightPx!! * 4
                        2 -> newY -= enemyPlaneHeightPx!! * 10
                    }
                    enemyPlane.y = newY
                    LogUtil.printLog(message = "enemyPlaneMove: newY $newY ")
                    LogUtil.printLog(message = "enemyPlaneMove: id = ${enemyPlane.id}，type = ${enemyPlane.type}， x = ${enemyPlane.x}， y = ${enemyPlane.y} ")
                    enemyPlane.init = true
                    enemyPlane.reBirth()
                }

                //飞出屏幕(位移到指定距离)，则死亡
                if (enemyPlane.y >= maxEnemyPlaneSpriteY) {
                    enemyPlane.init = false//这里不能在die方法里调用，否则碰撞检测爆炸后，敌机的位置马上变化了
                    enemyPlane.die()
                }
                //敌机位移
                enemyPlane.move()
            }
        }


    }

    /**
     * 针对敌机的碰撞检测
     */
    private fun onCollisionDetect(
        enemyPlane: EnemyPlane,
        onBombAnimChange: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                //如果使用了炸弹，会导致所有敌机的生命变成0，触发爆炸动画
                if (enemyPlane.isAlive() && enemyPlane.isNoPower()) {
                    //敌机死亡
                    enemyPlane.die()
                    //爆炸动画可显示
                    onBombAnimChange(true)
                }

                //敌机的大小
                val enemyPlaneWidthPx = dp2px(enemyPlane.width)
                val enemyPlaneHeightPx = dp2px(enemyPlane.height)

                //玩家飞机大小
                val playerPlane = playerPlaneStateFlow.value
                val playerPlaneWidthPx = dp2px(playerPlane.width)
                val playerPlaneHeightPx = dp2px(playerPlane.height)

                //如果敌机碰撞到了玩家飞机(碰撞检测要求，碰撞双方必须都在屏幕内)
                if (enemyPlane.isAlive() && playerPlane.x > 0 && playerPlane.y > 0 && enemyPlane.x > 0 && enemyPlane.y > 0 && SpriteUtil.isCollisionWithRect(
                        playerPlane.x,
                        playerPlane.y,
                        playerPlaneWidthPx!!,
                        playerPlaneHeightPx!!,
                        enemyPlane.x,
                        enemyPlane.y,
                        enemyPlaneWidthPx!!,
                        enemyPlaneHeightPx!!
                    )
                ) {
                    //玩家飞机爆炸，进入GameState.Dying状态，播放爆炸动画，动画结束后进入GameState.Over，弹出提示框，选择重新开始或退出
                    if (gameStateFlow.value == GameState.Running) {
                        if (playerPlane.isNoProtect()) {
                            onGameAction.die()
                        }
                    }

                }

                //子弹大小
                val bulletList = bulletListStateFlow.value
                if (bulletList.isEmpty()) {
                    return@withContext
                }
                val firstBullet = bulletList.first()
                val bulletSpriteWidthPx = dp2px(firstBullet.width)
                val bulletSpriteHeightPx = dp2px(firstBullet.height)

                //遍历子弹和敌机是否发生碰撞
                bulletList.forEach { bullet ->
                    //如果敌机存活且碰撞到了子弹(碰撞检测要求，碰撞双方必须都在屏幕内)
                    if (enemyPlane.isAlive() && bullet.isAlive() && bullet.x > 0 && bullet.y > 0 && SpriteUtil.isCollisionWithRect(
                            bullet.x,
                            bullet.y,
                            bulletSpriteWidthPx!!,
                            bulletSpriteHeightPx!!,
                            enemyPlane.x,
                            enemyPlane.y,
                            enemyPlaneWidthPx!!,
                            enemyPlaneHeightPx!!
                        )
                    ) {
                        bullet.die()
                        enemyPlane.beHit(bullet.hit)
                        //敌机无能量后就爆炸
                        if (enemyPlane.isNoPower()) {
                            //敌机死亡
                            enemyPlane.die()
                            //爆炸动画可显示
                            onBombAnimChange(true)
                            //爆炸动画是观察分数变化来触发的
                            onGameAction.score(gameScoreStateFlow.value + enemyPlane.value)
                            return@forEach
                        }
                    }
                }
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
     * 奖励下落
     */
    private fun onAwardMove(award: Award) {
        //获取屏幕宽高
        val widthPixels = getApplication<Application>().resources.displayMetrics.widthPixels
        val heightPixels = getApplication<Application>().resources.displayMetrics.heightPixels

        //奖励的大小和活动范围
        val awardWidthPx = dp2px(award.width)
        val awardHeightPx = dp2px(award.height)
        val maxAwardSpriteX = widthPixels - awardWidthPx!! //X轴屏幕宽度向左偏移一个身位
        val maxAwardSpriteY = heightPixels * 1.5 //屏幕高度

        //初始化起点
        if (!award.init) {
            award.startX = (0..maxAwardSpriteX).random()
            award.startY = -awardHeightPx!!.toInt()
            award.x = award.startX
            award.y = award.startY
            award.init = true
        }

        //移动指定的距离(这里是一个屏幕高度的距离)后
        if (award.isAlive() && award.y >= maxAwardSpriteY) {
            award.die()
        }

        //下落
        award.move()

        //玩家飞机大小
        val playerPlane = playerPlaneStateFlow.value
        val playerPlaneWidthPx = dp2px(playerPlane.width)
        val playerPlaneHeightPx = dp2px(playerPlane.height)

        //如果道具奖励碰撞到了玩家飞机(碰撞检测要求，碰撞双方必须都在屏幕内)
        if (playerPlane.isAlive() && playerPlane.x > 0 && playerPlane.y > 0 && award.isAlive() && award.x > 0 && award.y > 0 && SpriteUtil.isCollisionWithRect(
                playerPlane.x,
                playerPlane.y,
                playerPlaneWidthPx!!,
                playerPlaneHeightPx!!,
                award.x,
                award.y,
                awardWidthPx,
                awardHeightPx!!,
            )
        ) {
            onGameAction.award(award)
            award.die()
        }
    }

    /**
     * 游戏得分
     */
    private fun onGameScore(application: Application, score: Int) {
        //播放爆炸音效
        onPlayByRes(application, R.raw.explosion)

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


    /**
     * 获得道具奖励
     */
    private fun onGetAward(award: Award) {
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

    /**
     * 重置分数，玩家飞机重生
     */
    private fun onGameReset() {

        //分数归零
        onGameScoreStateFlowChange(0)

        //玩家飞机重生
        onPlayerPlaneReBirth()

    }


    /**
     * 播放音效
     */
    private fun onPlayByRes(context: Context, @RawRes resId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                SoundPoolUtil.getInstance(context.applicationContext)
                    .playByRes(resId)//播放res中的音频
            }
        }
    }


    //游戏动作
    val onGameAction = GameAction(
        start = {
            //游戏开始
            onGameStateFlowChange(GameState.Running)
        },
        reset = {
            //重置游戏分数
            onGameReset()
            //游戏等待
            onGameStateFlowChange(GameState.Waiting)
        },
        pause = {
            //游戏暂停
            onGameStateFlowChange(GameState.Paused)

        },
        movePlayerPlane = { x, y ->
            //玩家飞机移动到指定坐标
            onPlayerPlaneMove(x, y)
        },
        dragPlayerPlane = { dragAmount ->
            //玩家飞机拖拽
            onDragPlayerPlane(dragAmount)
        },
        score = { score ->
            //游戏得分
            onGameScore(application, score)
        },
        award = { award ->
            //获得获道具奖励
            onGetAward(award)
        },
        die = {
            //播放爆炸音效
            onPlayByRes(application, R.raw.explosion)
            onGameStateFlowChange(GameState.Dying)

        },
        over = {
            //游戏结束
            onGameStateFlowChange(GameState.Over)
        },
        exit = {
            //退出游戏
            onGameStateFlowChange(GameState.Exit)
        },
        destroyAllEnemy = {
            //摧毁所有敌机
            onDestroyAllEnemy()
        },
        moveEnemyPlane = {
            //敌机飞行
            onEnemyPlaneMove(it)
        },
        moveAward = {
            //奖励下落
            onAwardMove(it)
        },
        collisionDetect = { enemyPlane, onBombAnimChange ->
            run {
                //敌机和子弹，玩家飞机碰撞碰撞
                onCollisionDetect(enemyPlane, onBombAnimChange)
            }
        },
        createBullet = {
            //生成子弹
            onCreateBullet()
        },
        moveBullet = {
            //子弹射击
            onBulletMove(it)
        },
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