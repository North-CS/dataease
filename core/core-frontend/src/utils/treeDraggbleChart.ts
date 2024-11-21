import { dvNameCheck, moveResource } from '@/api/visualization/dataVisualization'
import { cloneDeep } from 'lodash-es'
const treeDraggbleChart = (state, key, type) => {
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

  const handleDragStart = node => {
    dragNodeId = node.data.id
    rawData = cloneDeep(state[key])
    dfsTreeNode(state[key], '0')
  }

  const allowDrop = (_, dropNode) => {
    return !dropNode.data?.leaf
  }

  const handleDrop = async (draggingNode, dropNode, dropType) => {
    const params = {
      id: draggingNode.data?.id,
      name: draggingNode.data?.name,
      nodeType: draggingNode.data?.leaf ? 'leaf' : 'folder',
      pid: '0',
      opt: 'move',
      type
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

    try {
      await dvNameCheck(params)
    } catch (error) {
      console.error(error)
    }
    delete params.opt

    if (dropType === 'inner') {
      params.pid = dropNode.data?.id
    } else {
      dfsTreeNodeBack(state[key], '0', params)
    }

    moveResource(params)
      .then(() => {
        state.originResourceTree = cloneDeep(state[key])
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

export { treeDraggbleChart }
