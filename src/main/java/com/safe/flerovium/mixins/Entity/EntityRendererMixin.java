package com.safe.flerovium.mixins.Entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.api.math.MatrixHelper;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.caffeinemc.mods.sodium.api.vertex.format.common.EntityVertex;
import net.caffeinemc.mods.sodium.client.render.immediate.model.EntityRenderer;
import net.caffeinemc.mods.sodium.client.render.immediate.model.ModelCuboid;
import net.caffeinemc.mods.sodium.client.util.Int2;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.safe.flerovium.Flerovium;

/**
 * Reimplements {@link EntityRenderer#renderCuboid} with view-space back-face culling.
 *
 * <p>At the point {@code renderCuboid} is invoked the {@link PoseStack.Pose} is the
 * model-to-view transform, so the camera sits at the origin and a face is invisible
 * whenever its outward normal points away from the origin, i.e.
 * {@code dot(normal, faceCenter) >= 0}.</p>
 *
 * <p>This is the mathematically-correct version of the culling found in the 26.1.2
 * source. The original decompiled code was unreliable: it dotted positions against a
 * <em>row</em> of the normal matrix (i.e. the transposed/inverse rotation) instead of a
 * <em>column</em>, and it used sums of opposite corners instead of real face centres,
 * which caused wrongly-culled faces on item frames / display cases. Here faces are
 * rejected using their actual transformed centre and the correctly-transformed normal
 * (column of the normal matrix). The fast vertex-packing path is Sodium's own
 * ({@code writeVertex}/{@code setVertex} shadows), so the only added cost is the cheap
 * per-face test, while up to half the vertices no longer need to be written to the GPU.
 */
@Mixin(value = EntityRenderer.class, remap = false, priority = 2005)
public abstract class EntityRendererMixin {
	@Shadow
	@Final
	private static int[] CUBE_FACE_NORMAL;
	@Shadow
	@Final
	private static long[] CUBE_VERTEX_XY;
	@Shadow
	@Final
	private static long[] CUBE_VERTEX_ZW;

	@Shadow
	private static void setVertex(int vertexIndex, float x, float y, float z, int color) {
	}

	@Shadow
	private static long writeVertex(long ptr, int vertexIndex, long packedUv, long packedOverlayLight, int packedNormal) {
		return 0L;
	}

	@Shadow
	private static void prepareNormalsIfChanged(PoseStack.Pose matrices) {
	}

	@Inject(method = "renderCuboid", at = @At("HEAD"), cancellable = true)
	private static void onRenderCuboid(PoseStack.Pose matrices, VertexBufferWriter writer, ModelCuboid cuboid, int light, int overlay, int color, CallbackInfo ci) {
		ci.cancel();

		int cullingMask = flerovium$prepareVertices(matrices, cuboid, color);
		prepareNormalsIfChanged(matrices);

		try (MemoryStack stack = MemoryStack.stackPush()) {
			long packedOverlayLight = Int2.pack(overlay, light);
			long vertexBuffer = stack.nmalloc(64, 864);
			int vertexCount = flerovium$emitQuads(vertexBuffer, cuboid, packedOverlayLight, cullingMask);
			if (vertexCount > 0) {
				writer.push(stack, vertexBuffer, vertexCount, EntityVertex.FORMAT);
			}
		}
	}

	@Unique
	private static int flerovium$prepareVertices(PoseStack.Pose matrices, ModelCuboid cuboid, int color) {
		Matrix4f pose = matrices.pose();
		float vxx = pose.m00() * cuboid.sizeX;
		float vxy = pose.m01() * cuboid.sizeX;
		float vxz = pose.m02() * cuboid.sizeX;
		float vyx = pose.m10() * cuboid.sizeY;
		float vyy = pose.m11() * cuboid.sizeY;
		float vyz = pose.m12() * cuboid.sizeY;
		float vzx = pose.m20() * cuboid.sizeZ;
		float vzy = pose.m21() * cuboid.sizeZ;
		float vzz = pose.m22() * cuboid.sizeZ;

		// Transform the 8 corners of the cuboid into view space. Reusing the pre-multiplied
		// edge vectors avoids 5/8 of the matrix-vector products (same trick as Sodium).
		float c000x = MatrixHelper.transformPositionX(pose, cuboid.originX, cuboid.originY, cuboid.originZ);
		float c000y = MatrixHelper.transformPositionY(pose, cuboid.originX, cuboid.originY, cuboid.originZ);
		float c000z = MatrixHelper.transformPositionZ(pose, cuboid.originX, cuboid.originY, cuboid.originZ);
		setVertex(0, c000x, c000y, c000z, color);

		float c100x = c000x + vxx;
		float c100y = c000y + vxy;
		float c100z = c000z + vxz;
		setVertex(1, c100x, c100y, c100z, color);

		float c110x = c100x + vyx;
		float c110y = c100y + vyy;
		float c110z = c100z + vyz;
		setVertex(2, c110x, c110y, c110z, color);

		float c010x = c000x + vyx;
		float c010y = c000y + vyy;
		float c010z = c000z + vyz;
		setVertex(3, c010x, c010y, c010z, color);

		float c001x = c000x + vzx;
		float c001y = c000y + vzy;
		float c001z = c000z + vzz;
		setVertex(4, c001x, c001y, c001z, color);

		float c101x = c100x + vzx;
		float c101y = c100y + vzy;
		float c101z = c100z + vzz;
		setVertex(5, c101x, c101y, c101z, color);

		float c111x = c110x + vzx;
		float c111y = c110y + vzy;
		float c111z = c110z + vzz;
		setVertex(6, c111x, c111y, c111z, color);

		float c011x = c010x + vzx;
		float c011y = c010y + vzy;
		float c011z = c010z + vzz;
		setVertex(7, c011x, c011y, c011z, color);

		int cullingMask = ((ModelCuboidAccessor) cuboid).getCullMask();

		// View-space back-face culling. The guards keep it strictly conservative:
		//  - only for cuboids at least 16 units in front of the camera (pose.m32() is the
		//    view-space Z of the cuboid origin, negative = in front);
		//  - only when the global model-view matrix carries no Z translation, i.e. the pose
		//    really is model->view (otherwise we fall back to Sodium's path, no culling).
		if (Flerovium.config.entityBackFaceCulling && pose.m32() <= -16.0F && RenderSystem.getModelViewMatrix().m32() == 0.0F) {
			Matrix3f n = matrices.normal();
			// Columns of the normal matrix are the view-space images of the model-space axes.
			float axX = n.m00, axY = n.m10, axZ = n.m20; // +X axis
			float ayX = n.m01, ayY = n.m11, ayZ = n.m21; // +Y axis
			float azX = n.m02, azY = n.m12, azZ = n.m22; // +Z axis

			// Face 0 (DOWN / -Y): corners {c000,c100,c101,c001}, normal = -Y
			if (flerovium$isBackFace(c000x, c000y, c000z, c100x, c100y, c100z, c101x, c101y, c101z, c001x, c001y, c001z, -ayX, -ayY, -ayZ)) {
				cullingMask &= ~(1 << 0);
			}
			// Face 1 (UP / +Y): corners {c010,c110,c111,c011}, normal = +Y
			if (flerovium$isBackFace(c010x, c010y, c010z, c110x, c110y, c110z, c111x, c111y, c111z, c011x, c011y, c011z, ayX, ayY, ayZ)) {
				cullingMask &= ~(1 << 1);
			}
			// Face 2 (EAST / +X): corners {c100,c110,c111,c101}, normal = +X
			if (flerovium$isBackFace(c100x, c100y, c100z, c110x, c110y, c110z, c111x, c111y, c111z, c101x, c101y, c101z, axX, axY, axZ)) {
				cullingMask &= ~(1 << 2);
			}
			// Face 3 (NORTH / -Z): corners {c000,c100,c110,c010}, normal = -Z
			if (flerovium$isBackFace(c000x, c000y, c000z, c100x, c100y, c100z, c110x, c110y, c110z, c010x, c010y, c010z, -azX, -azY, -azZ)) {
				cullingMask &= ~(1 << 3);
			}
			// Face 4 (WEST / -X): corners {c000,c010,c011,c001}, normal = -X
			if (flerovium$isBackFace(c000x, c000y, c000z, c010x, c010y, c010z, c011x, c011y, c011z, c001x, c001y, c001z, -axX, -axY, -axZ)) {
				cullingMask &= ~(1 << 4);
			}
			// Face 5 (SOUTH / +Z): corners {c001,c101,c111,c011}, normal = +Z
			if (flerovium$isBackFace(c001x, c001y, c001z, c101x, c101y, c101z, c111x, c111y, c111z, c011x, c011y, c011z, azX, azY, azZ)) {
				cullingMask &= ~(1 << 5);
			}
		}

		return cullingMask;
	}

	@Unique
	private static boolean flerovium$isBackFace(float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz, float nx, float ny, float nz) {
		float centerX = (ax + bx + cx + dx) * 0.25F;
		float centerY = (ay + by + cy + dy) * 0.25F;
		float centerZ = (az + bz + cz + dz) * 0.25F;
		return nx * centerX + ny * centerY + nz * centerZ >= 0.0F;
	}

	@Unique
	private static int flerovium$emitQuads(long buffer, ModelCuboid cuboid, long packedOverlayLight, int cullMask) {
		long ptr = buffer;
		int[] normals = cuboid.normals;
		int[] positions = cuboid.positions;
		long[] textures = cuboid.textures;
		int vertexCount = 0;

		for (int faceIndex = 0; faceIndex < 6; ++faceIndex) {
			if ((cullMask & 1 << faceIndex) != 0) {
				int elementOffset = faceIndex * 4;
				int packedNormal = CUBE_FACE_NORMAL[normals[faceIndex]];
				ptr = writeVertex(ptr, positions[elementOffset + 0], textures[elementOffset + 0], packedOverlayLight, packedNormal);
				ptr = writeVertex(ptr, positions[elementOffset + 1], textures[elementOffset + 1], packedOverlayLight, packedNormal);
				ptr = writeVertex(ptr, positions[elementOffset + 2], textures[elementOffset + 2], packedOverlayLight, packedNormal);
				ptr = writeVertex(ptr, positions[elementOffset + 3], textures[elementOffset + 3], packedOverlayLight, packedNormal);
				vertexCount += 4;
			}
		}

		return vertexCount;
	}
}

