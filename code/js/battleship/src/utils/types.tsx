
export type ProblemOutputModel = {
    type: string,
    title: string,
    detail: string,
}

export type SirenHome = {
    class: string[],
    properties: User[],
    entities: [],
    links: Links[],
    actions: []
}

export type SirenFleetSuccess = {
    class: string[],
    properties: string,
    entities: [],
    links: Links[],
    actions: []
}

export type GameHistory =
    {
        gameId: number,
        playerOne: User,
        playerTwo: User,
        gamePhase: Phase,
        roundNumber: number,
        playerWin: User,
    }

export interface Data {
    gameId: number,
    playerOne: string,
    playerTwo: string,
    gamePhase: string,
    roundNumber: number,
    playerWin: string,
}

export type SirenGameHistory = {
    class: string[],
    properties: GameHistory[],
    entities: [],
    links: Links[],
    actions: []
}

export type SirenGetFleet = {
    class: string[],
    properties: Array<Array<PositionStateBoard>>,
    entities: [],
    links: Links[],
    actions: []
}

export type SirenShoot = {
    class: string[],
    properties: DefaultAnswerModel,
    entities: [],
    links: Links[],
    actions: []
}

export type Phase = {
    id: number,
    name: string
}
export type SirenGetCurrentGamePhase = {
    class: string[],
    properties: Phase,
    entities: [],
    links: Links[],
    actions: []
}

export type DefaultAnswerModel = {
    error?: ProblemOutputModel,
    message?: String
}

export type Game = {
    gameId: number
}
export type Links = {
    rel: string[],
    href: String
}
export type SirenGetUserCurrentGame = {
    class: string[],
    properties: Game,
    entities: [],
    links: Links[],
    actions: []
}

export type User =
    {
        id: number,
        username: string,
        email: string
    }


export type UserRank =
    {
        user: User,
        playedGames: number,
        winGames: number,
        lostGames: number,
        rankPoints: number
    }


export type SirenGetRanking = {
    class: string[],
    properties: UserRank[],
    entities: [],
    links: Links[],
    actions: []
}

export function convertToData(histories: GameHistory[]): Data[] {
    return histories.map(history => {
        var playerWin = 'Game in progress'
        if (history.playerWin == null) {
        }
        else {
            playerWin = history.playerWin.username
        }
        return {
            gameId: history.gameId,
            playerOne: history.playerOne.username,
            playerTwo: history.playerTwo.username,
            gamePhase: history.gamePhase.name,
            roundNumber: history.roundNumber,
            playerWin: playerWin
        }
    });
}

//Help build a table https://reactjs.org/docs/fragments.html
enum ShipType {
    CARRIER,
    BATTLESHIP,
    SUBMARINE,
    CRUISER,
    DESTROYER
  };
  export type PositionStateBoard = {
    boardPosition: Position,
    wasShoot: Boolean,
    wasShip?: Boolean,
    shipType?: ShipType,
    shipLayout?: String
  }
  
  export type BoardT = {
    cells: Array<Array<PositionStateBoard>>,
  }
  
  export const ShipTypeSize = {
    CARRIER: 5,
    BATTLESHIP: 4,
    SUBMARINE: 3,
    CRUISER: 3,
    DESTROYER: 2
  };
  
  // Carrier -> Size: 5 purple
  // Battleship -> Size: 4 orange
  // Submarine -> Size: 3 blue
  // Cruiser -> Size: 3 yellow
  // Destroyer -> Size: 2 green
  //LEFT RIGHT UP DOWN

export type Ship = {
    shipName: string,
    shipChar: string,
    color: string,
  }
  export type Position = {
    row: number,
    col: number
  }
  export type ShipAndLayout = {
    shipType: string,
    shipLayout: string,
    referencePoint: Position
  }
  
  export type LayoutView = {
    shipLayout: string,
    selected: boolean
  }
  export type ShipAndLayoutsView = {
    ship: Ship,
    shipLayout: Array<LayoutView>,
    selected: boolean,
  }

  export function initilize_Ships_And_Layouts_View(): Array<ShipAndLayoutsView> {
    const shipsAndLayoutsView = []
    const ships = [
      { shipName: 'CARRIER', shipChar: 'AAAAA', color: 'purple' },
      { shipName: 'BATTLESHIP', shipChar: 'BBBB', color: 'orange' },
      { shipName: 'SUBMARINE', shipChar: 'SSS', color: 'blue' },
      { shipName: 'CRUISER', shipChar: 'UUU', color: 'yellow' },
      { shipName: 'DESTROYER', shipChar: 'DD', color: 'green' }
    ]
    for (var i = 0; i < ships.length; i++) {
      shipsAndLayoutsView[i] = {}
      shipsAndLayoutsView[i].ship = ships[i];
      shipsAndLayoutsView[i].shipLayout = [
        { shipLayout: 'LEFT', selected: false },
        { shipLayout: 'RIGHT', selected: false },
        { shipLayout: 'UP', selected: false },
        { shipLayout: 'DOWN', selected: false }
      ]
      shipsAndLayoutsView[i].selected = true
    }
    return shipsAndLayoutsView
  }