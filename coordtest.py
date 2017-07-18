import numpy as np

arr = np.zeros((10, 10))
coords = [(0, 0), (1, 0), (2, 0), (3, 0), (4, 0), (5, 0), (0, 1)]

minX, minZ = 0, 0
maxX, maxZ = 9, 9
def get_coords_minecraft(x, z, coordBaseMode):
    if coordBaseMode == 0:
        return (minX + x, minZ + z)
    if coordBaseMode == 1:
        return (maxX - z, minZ + x)
    if coordBaseMode == 2:
        return (minX + x, maxZ - z)
    if coordBaseMode == 3:
        return (minX + z, minZ + x)
    return None
    
def get_coords_sane(x, z, coordBaseMode):
    if coordBaseMode == 0:
        return (minX + x, minZ + z)
    if coordBaseMode == 1:
        return (maxX - z, minZ + x)
    if coordBaseMode == 2:
        return (maxX - x, maxZ - z)
    if coordBaseMode == 3:
        return (minX + z, maxZ - x)
        #return (5, 5)
    return None
        
for m in [0, 1, 2, 3]:
    for (x, z) in coords:
        #xC, zC = get_coords_minecraft(x, z, m)
        xC, zC = get_coords_sane(x, z, m)
        arr[xC, zC] = (m + 1)

print arr
