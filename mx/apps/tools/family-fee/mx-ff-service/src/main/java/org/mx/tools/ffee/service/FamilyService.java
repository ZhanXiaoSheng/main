package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.Family;

import java.io.File;
import java.io.InputStream;

public interface FamilyService {
    Family createFamily(Family family, String openId, String ownerRole);

    Family modifyFamily(Family family);

    Family getFamily(String familyId);

    Family joinFamily(String familyId, String role, String accountId);

    String changeFamilyAvatar(String familyId, InputStream in);

    File getFamilyAvatar(String familyId);

    File getFamilyQrCode(String familyId);
}
