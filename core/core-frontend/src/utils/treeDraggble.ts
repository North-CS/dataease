import { cloneDeep } from 'lodash-es'

const treeDraggble = (state, key, req, type, originResourceTree) => {
  let dragNodeParentId = ''
  let dragNodeId = ''
  let dragNodeIndex = 0
  let rawData = []

  const dfsTreeNode = (arr, parentId) => {
    arr.forEach((element, index) => {
      if (element.id === dragNodeId) {
        dragNodeIndex = index
        dragNodeParentId = parentId
      }
      if (element.children?.length) {
        dfsTreeNode(element.children, element.id)
      }
    })
  }

  const dfsTreeNodeBack = (arr, parentId, params) => {
    arr.forEach(element => {
      if (element.id === params.id) {
        params.pid = parentId
      }
      if (element.children?.length) {
        dfsTreeNodeBack(element.children, element.id, params)
      }
    })
  }

  const dfsTreeNodeSaveLevel = (arr, idArr) => {
    arr.forEach(element => {
      const index = idArr.findIndex(ele => {
        return ele === element.id
      })
      if (index !== -1) {
        idArr.splice(index, 1)
      }
      if (element.children?.length && idArr.length === 2) {
        dfsTreeNodeSaveLevel(element.children, idArr)
      }
    })
  }

  const dfsTreeNodeDel = (arr, node) => {
    arr.forEach((element, index) => {
      if (element.id === node.id) {
        arr.splice(index, 1)
      }
      if (element.children?.length) {
        dfsTreeNodeDel(element.children, node)
      }
    })
  }

  const dfsTreeNodeReset = (arr, node) => {
    arr.forEach(element => {
      if (element.id === dragNodeParentId) {
        element.children.splice(dragNodeIndex, 0, node)
      }
      if (element.children?.length) {
        dfsTreeNodeReset(element.children, node)
      }
    })
  }

  const handleDragStart = node => {
    dragNodeId = node.data.id
    rawData = cloneDeep(state[key])
    dfsTreeNode(state[key], '0')
  }

  const allowDrop = (_, dropNode) => {
    return !dropNode.data?.leaf
  }

  const handleDrop = (draggingNode, dropNode, dropType) => {
    const params = {
      id: draggingNode.data?.id,
      name: draggingNode.data?.name,
      nodeType: draggingNode.data?.leaf ? type : 'folder',
      pid: '0',
      action: 'move'
    }

    if (dropType !== 'inner') {
      const idArr = [params.id, dropNode.data?.id]
      dfsTreeNodeSaveLevel(rawData, idArr)
      if (idArr.length === 0) {
        dfsTreeNodeDel(state[key], draggingNode.data)
        setTimeout(() => {
          if (dragNodeParentId === '0') {
            state[key].splice(dragNodeIndex, 0, draggingNode.data)
          } else {
            dfsTreeNodeReset(state[key], draggingNode.data)
          }
        }, 0)
        return
      }
    }

    if (dropType === 'inner') {
      params.pid = dropNode.data?.id
    } else {
      dfsTreeNodeBack(state[key], '0', params)
    }

    req(params)
      .then(() => {
        originResourceTree.value = cloneDeep(state[key])
      })
      .catch(() => {
        if (dragNodeParentId === '0') {
          state[key].splice(dragNodeIndex, 0, draggingNode.data)
          return
        }

        dfsTreeNodeReset(state[key], draggingNode.data)
      })
  }

  return {
    handleDrop,
    allowDrop,
    handleDragStart
  }
}

export { treeDraggble }
